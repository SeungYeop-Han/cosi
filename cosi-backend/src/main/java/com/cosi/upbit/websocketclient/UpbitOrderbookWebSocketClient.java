package com.cosi.upbit.websocketclient;

import com.cosi.upbit.dto.MarketInfo;
import com.cosi.upbit.mirror.UpbitMarkets;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class UpbitOrderbookWebSocketClient extends WebSocketClient {

    // 현재 거래 가능한 업비트 종목
    private final UpbitMarkets upbitMarkets;

    // ToDo: 호가 내부 API 인터페이스 타입 빈 필드 추가

    public UpbitOrderbookWebSocketClient(URI serverUri, UpbitMarkets upbitMarkets) {
        super(serverUri);
        this.upbitMarkets = upbitMarkets;

        // 시작
        connect();
    }

    private String generateRequestBody(List<MarketInfo> marketInfoList) {
        // 티켓
        String base = "[{\"ticket\":\"" + UUID.randomUUID() + "\"},";
        StringBuilder requestBody = new StringBuilder(base);

        // 마켓 코드 문자열 생성: "{quoteCurrencyCode}-{baseCurrencyCode}","{...}-{...}",...
        StringBuilder marketCodes = new StringBuilder();
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
        requestBody.append("{\"type\":\"orderbook\",\"codes\":[").append(marketCodes);

        // 괄호 닫기
        requestBody.append("]}, {\"format\":\"SIMPLE\"}]");

        return requestBody.toString();
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("***** UpbitTicker 웹소켓 연결 성공 *****");

        // 업비트 종목 가져오기
        List<MarketInfo> marketInfoList = upbitMarkets.findAll();

        // 새로운 요청 보내기(PING 을 제외한 이전 요청은 더 이상 전송되지 않음)
        send(generateRequestBody(marketInfoList));
    }

    @Override
    public void onMessage(String message) {
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        String s = new String(
                /* bytes */ bytes.array(),
                /* offset */ bytes.position(),
                /* length */ bytes.limit()
        );

        // 수신한 문자열로부터 마켓 코드 추출(역직렬화 로직은 수행 안 함)
        String marketCode = s.substring(24, s.indexOf('\"', 24));

        // ToDo: 실시간 업데이트
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onError(Exception ex) {

    }
}
