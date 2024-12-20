package com.cosi.upbit.config;

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
}
