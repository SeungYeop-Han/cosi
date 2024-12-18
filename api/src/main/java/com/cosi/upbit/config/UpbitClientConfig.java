package com.cosi.upbit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.DefaultUriBuilderFactory.EncodingMode;

@Configuration
public class UpbitClientConfig {

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
