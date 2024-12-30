package com.cosi.api.market;

import com.cosi.upbit.mirror.UpbitMarkets;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/markets")
public class MarketRestController {

    private final UpbitMarkets upbitMarkets;

    @Autowired
    public MarketRestController(UpbitMarkets upbitMarkets) {
        this.upbitMarkets = upbitMarkets;
    }

    @GetMapping
    public ResponseEntity<byte[]> getMarkets() throws IOException {
        byte[] body = upbitMarkets.getGzipCompressedMarketListJson();
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_ENCODING, "gzip")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }
}
