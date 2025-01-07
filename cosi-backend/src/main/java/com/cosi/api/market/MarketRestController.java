package com.cosi.api.market;

import com.cosi.api.exception.BadRequestException;
import com.cosi.upbit.dto.TickerStatistics;
import com.cosi.upbit.mirror.UpbitMarkets;
import com.cosi.upbit.mirror.UpbitTicker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MarketRestController {

    private final UpbitMarkets upbitMarkets;
    private final UpbitTicker upbitTicker;

    @Autowired
    public MarketRestController(UpbitMarkets upbitMarkets, UpbitTicker upbitTicker) {
        this.upbitMarkets = upbitMarkets;
        this.upbitTicker = upbitTicker;
    }

    @GetMapping("/markets/all")
    public ResponseEntity<byte[]> getMarkets() {
        byte[] body = upbitMarkets.getGzipCompressedMarketListJson();
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_ENCODING, "gzip")
                .contentType(MediaType.APPLICATION_JSON)
                .cacheControl(CacheControl.noCache())
                .eTag(upbitMarkets.getEtag())
                .body(body);
    }

    @GetMapping("/ticker/statistics")
    public ResponseEntity<TickerStatistics> getMarketStatistics(@RequestParam("marketCode") String marketCode) {

        TickerStatistics statistics = upbitTicker.getStatisticsSnapshot(marketCode).orElseThrow(() -> {
            // ToDo: BadRequestException 을 확장하는 특화 예외 클래스 정의 (ex. MarketNotFoundException)
            throw new BadRequestException(HttpStatus.NOT_FOUND, marketCode + "에 해당하는 종목을 찾을 수 없습니다. 코드는 대문자로만 이루어져있습니다. 다시 한 번 확인해주세요.");
        });

        return ResponseEntity
                .ok()
                .body(statistics);
    }
}
