package com.cosi.upbit.websocketclient;

import com.cosi.upbit.dto.MarketInfo;
import com.cosi.upbit.dto.TickerRealtimeQuotes;
import com.cosi.upbit.dto.TickerStatistics;
import com.cosi.upbit.mirror.UpbitMarkets;
import com.cosi.upbit.mirror.UpbitTicker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

/**
 * 업비트 공식 웹소켓 API 로부터 시세 정보를 가져옵니다.
 * <br><br>
 * 대상 종목은 {@link com.cosi.upbit.mirror.UpbitMarkets} 로부터 얻은 모든 종목입니다.
 * <br><br>
 * 참고 자료: <a href="https://docs.upbit.com/reference">API 레퍼런스</a>
 */
public class UpbitTickerWebSocketClient extends WebSocketClient implements UpbitTicker {

    /////////////////////////////////////
    ////////// ↓ UpbitTicker ↓ //////////
    /////////////////////////////////////

    Map<String, TickerStatistics> tickerStatisticsMap = new HashMap<>();
    Map<String, TickerRealtimeQuotes> tickerRealtimeQuotesMap = new HashMap<>();

    // ToDo: Collections.unmodifiableMap 으로 변환?

    @Override
    public Map<String, TickerStatistics> getStatistics() {
        return tickerStatisticsMap;
    }

    @Override
    public Optional<TickerStatistics> getStatisticsOf(String baseCurrencyCode, String quoteCurrencyCode) {
        return Optional.ofNullable(tickerStatisticsMap.get(baseCurrencyCode + "-" + quoteCurrencyCode));
    }

    @Override
    public Map<String, TickerRealtimeQuotes> getRealtimeQuotes() {
        return tickerRealtimeQuotesMap;
    }

    @Override
    public Optional<TickerRealtimeQuotes> getRealtimeQuotesOf(String baseCurrencyCode, String quoteCurrencyCode) {
        return Optional.ofNullable(tickerRealtimeQuotesMap.get(baseCurrencyCode + "-" + quoteCurrencyCode));
    }

    /////////////////////////////////////////
    ////////// ↓ WebSocketClient ↓ //////////
    /////////////////////////////////////////

    private final Gson gson = new GsonBuilder().create();

    private final UpbitMarkets upbitMarkets;

    private final int UPDATE_STATISTICS_PERIOD_IN_SECONDS;
    private long couldUpdateTickerStatisticsAfterThisPoint;  // timestamp

    public UpbitTickerWebSocketClient(URI serverUri, UpbitMarkets upbitMarkets, int UPDATE_STATISTICS_PERIOD_IN_SECONDS) {

        super(serverUri);

        if (upbitMarkets == null || UPDATE_STATISTICS_PERIOD_IN_SECONDS < 1) {
            throw new IllegalArgumentException("upbitMarkets 가 null 이거나, pollingPeriodInSeconds 가 1 보다 작습니다.");
        }

        this.upbitMarkets = upbitMarkets;
        this.UPDATE_STATISTICS_PERIOD_IN_SECONDS = UPDATE_STATISTICS_PERIOD_IN_SECONDS;

        // 처음 시작시 무조건 폴링을 수행 할 것임
        this.couldUpdateTickerStatisticsAfterThisPoint = System.currentTimeMillis();

        // 시작
        connect();
    }

    public void sendRequestBody() {

        // 티켓
        String base = "[{\"ticket\":\"" + UUID.randomUUID() + "\"},";
        StringBuilder requestBody = new StringBuilder(base);

        // 마켓 코드 문자열 생성: "{quoteCurrencyCode}-{baseCurrencyCode}","{...}-{...}",...
        StringBuilder marketCodes = new StringBuilder();
        List<MarketInfo> marketInfoList = upbitMarkets.findAll();
        for (MarketInfo marketInfo : marketInfoList) {
            marketCodes
                    .append("\"")
                    .append(marketInfo.getQuoteCurrencyCode())
                    .append("-")
                    .append(marketInfo.getBaseCurrencyCode())
                    .append("\",");
        }
        marketCodes.deleteCharAt(marketCodes.length()-1); // ',' 지우기

        // 마켓 코드 문자열 추가
        requestBody.append("{\"type\":\"ticker\",\"codes\":[").append(marketCodes);

        // 괄호 닫기
        requestBody.append("]}, {\"format\":\"SIMPLE\"}]");

        // 새로운 요청 보내기(PING 을 제외한 이전 요청은 더 이상 전송되지 않음)
        send(requestBody.toString());
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("***** UpbitTicker 웹소켓 연결 성공 *****");
        sendRequestBody();
    }

    private boolean couldUpdateTickerStatistics() {
        return couldUpdateTickerStatisticsAfterThisPoint < System.currentTimeMillis();
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        String s = new String(
                /* bytes */ bytes.array(),
                /* offset */ bytes.position(),
                /* length */ bytes.limit()
        );

        // 실시간 업데이트
        TickerRealtimeQuotes tickerRealtimeQuotes = gson.fromJson(s, TickerRealtimeQuotes.class);
        tickerRealtimeQuotesMap.put(tickerRealtimeQuotes.getCode(), tickerRealtimeQuotes);

        // 주기적으로 폴링
        if (couldUpdateTickerStatistics()) {
            TickerStatistics tickerStatistics = gson.fromJson(s, TickerStatistics.class);
            tickerStatisticsMap.put(tickerStatistics.getCode(), tickerStatistics);
            couldUpdateTickerStatisticsAfterThisPoint += (long) UPDATE_STATISTICS_PERIOD_IN_SECONDS * 1000;
        }
    }

    @Override
    public void onMessage(String message) {}

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("***** UpbitTicker 웹소켓 종료 성공 *****");
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("UpbitTicker 실행 중 예외 발생: " + ex.getMessage());
        System.err.println(" >>> cause: " + ex.getCause());
    }
}