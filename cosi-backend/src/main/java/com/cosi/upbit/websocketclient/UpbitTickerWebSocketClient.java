package com.cosi.upbit.websocketclient;

import com.cosi.upbit.dto.MarketInfo;
import com.cosi.upbit.dto.TickerQuotes;
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
public class UpbitTickerWebSocketClient extends WebSocketClient {

    // 현재 거래 가능한 업비트 종목
    private final UpbitMarkets upbitMarkets;

    // Ticker 통계량(Statistics) 정보 갱신 주기(초)
    private final int STATISTICS_UPDATE_PERIOD_IN_SECONDS;

    // 수신한 데이터를 이용하여 갱신할 업비트 Ticker
    private final UpbitTicker upbitTicker;

    // 웹소켓을 통해 수신한 Ticker 문자열을 객체로 역직렬화 하기 위한 Gson 객체
    private final Gson gson = new GsonBuilder().create();

    // 각 종목의 업데이트 기준 시각(이 시점 이전에는 업데이트 불가능)
    private Map<String, Long> updateBaseTimestampMap = new HashMap<>();

    public UpbitTickerWebSocketClient(
            URI serverUri,
            UpbitMarkets upbitMarkets,
            int STATISTICS_UPDATE_PERIOD_IN_SECONDS,
            UpbitTicker upbitTicker) {

        super(serverUri);

        if (upbitMarkets == null || STATISTICS_UPDATE_PERIOD_IN_SECONDS < 1) {
            throw new IllegalArgumentException("upbitMarkets 가 null 이거나, pollingPeriodInSeconds 가 1 보다 작습니다.");
        }

        this.upbitMarkets = upbitMarkets;
        this.STATISTICS_UPDATE_PERIOD_IN_SECONDS = STATISTICS_UPDATE_PERIOD_IN_SECONDS;
        this.upbitTicker = upbitTicker;

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

        // 각 종목의 업데이트 기준 시각을 현재 시각으로 설정
        Long value = System.currentTimeMillis();
        for (MarketInfo marketInfo : marketInfoList) {
            String key = marketInfo.getQuoteCurrencyCode() + "-" + marketInfo.getBaseCurrencyCode();
            updateBaseTimestampMap.put(key, value);
        }

        // 새로운 요청 보내기(PING 을 제외한 이전 요청은 더 이상 전송되지 않음)
        send(requestBody.toString());
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("***** UpbitTicker 웹소켓 연결 성공 *****");
        sendRequestBody();
    }

    /**
     * @param marketCode {quoteCurrencyCode}-{baseCurrencyCode} (ex. KRW-BTC)
     * @return
     * <b>false</b>: upbitMarketCode 에 해당하는 종목을 찾을 수 없거나 마지막으로 업데이트 한 시점으로부터
     * UPDATE_STATISTICS_PERIOD_IN_SECONDS 초가 지나기 전인 경우 false 를 반환합니다.<br>
     * <b>true</b>: upbitMarketCode 에 해당하는 종목을 마지막으로 업데이트 한 시점으로부터 UPDATE_STATISTICS_PERIOD_IN_SECONDS 초가
     * 지난 경우 true 를 반환합니다.
     */
    private boolean couldUpdateTickerStatistics(String marketCode) {
        if (marketCode == null || updateBaseTimestampMap.get(marketCode) == null) {
            return false;
        }

        return updateBaseTimestampMap.get(marketCode) < System.currentTimeMillis();
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        String s = new String(
                /* bytes */ bytes.array(),
                /* offset */ bytes.position(),
                /* length */ bytes.limit()
        );

        // 실시간 업데이트
        TickerQuotes tickerQuotes = gson.fromJson(s, TickerQuotes.class);
        upbitTicker.updateQuotes(tickerQuotes);

        // 주기적으로 업데이트
        String marketCode = tickerQuotes.getCode();
        if (couldUpdateTickerStatistics(marketCode)) {
            TickerStatistics tickerStatistics = gson.fromJson(s, TickerStatistics.class);
            upbitTicker.updateStatistics(tickerStatistics);
            updateBaseTimestampMap.compute(marketCode, (k, v) -> v + (long) STATISTICS_UPDATE_PERIOD_IN_SECONDS * 1000);
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