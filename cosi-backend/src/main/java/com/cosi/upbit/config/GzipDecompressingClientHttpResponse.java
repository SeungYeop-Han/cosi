package com.cosi.upbit.config;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;

public class GzipDecompressingClientHttpResponse implements ClientHttpResponse {

    private final ClientHttpResponse response;

    public GzipDecompressingClientHttpResponse(ClientHttpResponse response) {
        this.response = response;
    }

    /**
     * @implNote 응답의 바디가 gzip 압축된 경우 압축 해제 후 GzipInputStream 으로 래핑 후 반환하고, 아닌 경우 그대로 반환합니다.
     */
    @Override
    public InputStream getBody() throws IOException {
        InputStream body = response.getBody();
        if (isGzipped(response)) {
            return new GZIPInputStream(new BufferedInputStream(body));
        }
        return body;
    }

    private boolean isGzipped(ClientHttpResponse response) {
        String contentEncoding = response.getHeaders().getFirst("Content-Encoding");
        return contentEncoding != null && contentEncoding.toLowerCase().contains("gzip");
    }


    @Override
    public HttpStatusCode getStatusCode() throws IOException {
        return response.getStatusCode();
    }

    @Override
    public String getStatusText() throws IOException {
        return response.getStatusText();
    }

    @Override
    public void close() {
        response.close();
    }

    @Override
    public org.springframework.http.HttpHeaders getHeaders() {
        return response.getHeaders();
    }
}
