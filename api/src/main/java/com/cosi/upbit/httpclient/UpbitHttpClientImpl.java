package com.cosi.upbit.httpclient;

import com.cosi.upbit.dto.CryptocurrencyPairInfo;
import java.net.URI;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Spring RestClient 를 사용하여 업비트 API 서버와 통신합니다.<br><br>
 * 본 클래스는 {@link com.cosi.upbit.config.UpbitClientConfig} 에 빈으로 등록되어 있습니다.
 */
public class UpbitHttpClientImpl implements UpbitHttpClient {

    // 주입됨
    private final RestClient restClient;

    // 응답된 종목 리스트는 자주 변하지 않으므로 캐시하여 최적화
    private List<CryptocurrencyPairInfo> cachedCryptocurrencyPairInfoList = null;

    // 종목 리스트의 캐시 유효성 판단을 위한 etag
    private String etagOfCryptocurrencyPairInfoList;

    /**
     * @param restClient gzip 압축된 응답을 처리할 수 있어야 함
     */
    public UpbitHttpClientImpl(RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * @implNote 응답은 캐시됩니다.
     * @return 전체 가상화폐 종목 리스트
     */
    @Override
    public List<CryptocurrencyPairInfo> getCryptocurrencyPairInfoList() {

        // 요청 URI
        URI uri = uriOfGetCryptocurrencyPairInfoList();

        // 요청 전송 -> 응답 수신 -> 응답 역직렬화
        ResponseEntity<List<CryptocurrencyPairInfo>> responseEntity = restClient
                .get()
                .uri(uri)
                // 캐시 헤더 설정
                .headers(httpHeaders -> {
                    if (etagOfCryptocurrencyPairInfoList != null) {
                        httpHeaders.setCacheControl("max-age=0");
                        httpHeaders.setIfNoneMatch(etagOfCryptocurrencyPairInfoList);
                    }
                })
                // 동기적으로 응답 수신
                .retrieve()
                // 예외 처리
                .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    String errorMessage = new StringBuilder()
                            .append("UpbitHttpClientImpl.getMarketCodes 메서드를 실행하는 중 예외가 발생했습니다.")
                            .append("url: ").append(request.getURI())
                            .append("status code: ").append(response.getStatusCode())
                            .append("status text: ").append(response.getStatusText())
                            .toString();
                    throw new RuntimeException(errorMessage);
                }))
                // 캐시 헤더 확인을 위해 ResponseEntity 로 역직렬화
                .toEntity(new ParameterizedTypeReference<List<CryptocurrencyPairInfo>>() {});

        // not modified -> 캐시 반환
        if (responseEntity.getStatusCode().equals(HttpStatusCode.valueOf(304))) {
            return cachedCryptocurrencyPairInfoList;
        }

        // modified -> etag 갱신 -> 데이터 캐시 -> 반환
        this.etagOfCryptocurrencyPairInfoList = responseEntity.getHeaders().getETag();
        cachedCryptocurrencyPairInfoList = responseEntity.getBody();
        return cachedCryptocurrencyPairInfoList;
    }
    private URI uriOfGetCryptocurrencyPairInfoList() {
        return UriComponentsBuilder
                .fromUriString("https://crix-static.upbit.com")
                .path("/crix_master")
                .build()
                .toUri();
    }
}
