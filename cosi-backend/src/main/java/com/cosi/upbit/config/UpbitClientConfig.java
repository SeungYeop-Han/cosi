package com.cosi.upbit.config;

import com.cosi.upbit.httpclient.UpbitHttpClient;
import com.cosi.upbit.httpclient.UpbitHttpClientImpl;
import com.cosi.upbit.mirror.UpbitMarkets;
import com.cosi.upbit.mirror.UpbitMarketsImpl;
import com.cosi.upbit.mirror.UpbitOrderbook;
import com.cosi.upbit.mirror.UpbitTicker;
import com.cosi.upbit.websocketclient.UpbitOrderbookWebSocketClient;
import com.cosi.upbit.websocketclient.UpbitTickerWebSocketClient;
import java.net.URI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.DefaultUriBuilderFactory.EncodingMode;

@Configuration
public class UpbitClientConfig {

    /**
     * 업비트 RestClient 빈 등록
     * <ul>
     *     <li>경로 변수 값에 슬래시가 포함되는 경우가 있으므로, uriBuilderFactory 를 EncodingMode.NONE 으로 설정한다.
     *     <li>gzip 응답을 해제하는 인터셉터를 등록한다.
     * </ul>
     * @return 비동기 HTTP 통신을 위해 사용할 스프링 RestClient 빈 객체
     */
    @Bean
    public RestClient restClient() {

        // 경로 변수 값이 슬래시(/)를 포함하는 경우, 슬래시 문자를 url 인코딩 하면 안 된다.
        // 이를 위해서 인코딩 모드를 NONE 으로 설정한다.
        DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory();
        uriBuilderFactory.setEncodingMode(EncodingMode.NONE);

        return RestClient.builder()
                .uriBuilderFactory(uriBuilderFactory)
                .requestInterceptor((request, body, execution) -> {
                    ClientHttpResponse response = execution.execute(request, body);
                    return new GzipDecompressingClientHttpResponse(response);
                })
                .build();
    }

    /**
     * @param restClient Gzip 형식의 응답을 디코딩할 수 있는 restClient
     * @return RestClient 에 의존하는 UpbitHttpClient 빈을 반환한다.
     */
    @Bean
    public UpbitHttpClient upbitHttpClient(RestClient restClient) {
        return new UpbitHttpClientImpl(restClient);
    }

    /**
     * @param upbitHttpClient
     * @return UpbitHttpClient 빈에 의존하는 UpbitMarket 빈을 반환합니다.
     */
    @Bean
    public UpbitMarkets upbitMarkets(UpbitHttpClient upbitHttpClient) {

        final int CACHE_MAX_AGE_IN_SECONDS = 600;
        return new UpbitMarketsImpl(upbitHttpClient, CACHE_MAX_AGE_IN_SECONDS);
    }

    /**
     * @param upbitMarkets
     * @return 업비트 시세(티커) 정보를 수신하고 UpbitTicker 빈을 갱신하는, UpbitTickerWebSocketClient 빈
     */
    @Bean
    public UpbitTickerWebSocketClient upbitTickerWebSocketClient(UpbitMarkets upbitMarkets, UpbitTicker upbitTicker) {

        final int STATISTICS_UPDATE_PERIOD_IN_SECONDS = 30;
        return new UpbitTickerWebSocketClient(
                URI.create("wss://api.upbit.com/websocket/v1"),
                upbitMarkets,
                STATISTICS_UPDATE_PERIOD_IN_SECONDS,
                upbitTicker);
    }

    /**
     * @param upbitMarkets
     * @return 업비트 호가 정보를 수신하고 UpbitOrderbook 빈을 갱신하는 UpbitOrderbookWebSocketClient 빈
     */
    @Bean
    public UpbitOrderbookWebSocketClient upbitOrderbookWebSocketClient(UpbitMarkets upbitMarkets,
                                                                       UpbitOrderbook upbitOrderbook) {
        return new UpbitOrderbookWebSocketClient(
                URI.create("wss://api.upbit.com/websocket/v1"),
                upbitMarkets,
                upbitOrderbook
        );
    }
}
