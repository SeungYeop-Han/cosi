package com.cosi.upbit.httpclient;

import org.springframework.web.client.RestClient;

/**
 * Spring RestClient 를 사용하여 업비트 API 서버와 통신합니다.<br><br>
 * 본 클래스는 {@link com.cosi.upbit.config.UpbitClientConfig} 에 빈으로 등록되어 있습니다.
 */
public class UpbitHttpClientImpl implements UpbitHttpClient {

    // 주입됨
    private final RestClient restClient;

    /**
     * @param restClient gzip 압축된 응답을 처리할 수 있어야 함
     */
    public UpbitHttpClientImpl(RestClient restClient) {
        this.restClient = restClient;
    }
}
