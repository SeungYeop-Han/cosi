package com.cosi.api.market;

import com.cosi.api.exception.BadRequestException;
import com.cosi.upbit.dto.TickerQuotes;
import com.cosi.upbit.dto.TickerStatistics;
import com.cosi.upbit.dto.TickerStatisticsView;
import com.cosi.upbit.mirror.UpbitMarkets;
import com.cosi.upbit.mirror.UpbitTicker;
import com.fasterxml.jackson.annotation.JsonView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    /**
     * <h1>거래 가능한 모든 종목의 개요 정보 목록</h1>
     */
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

    /**
     * <h1>종목 시세 스냅샷(호가통화별)</h1>
     */
    @GetMapping("/ticker/quotes/{quoteCurrencyCode}")
    public ResponseEntity<Collection<TickerQuotes>> getQuotesSnapshotsOfMarketsWhich(
            @PathVariable("quoteCurrencyCode") String quoteCurrencyCode) {

        Map<String, TickerQuotes> map
                = upbitTicker.getQuotesMapWhichQuoteCurrencyCodeIs(quoteCurrencyCode);

        if (map == null) {
            throw new BadRequestException(
                    HttpStatus.NOT_FOUND,
                    quoteCurrencyCode + " 를 호가통화로 가지는 종목에 대한 시세 Map 을 찾을 수 없습니다.");
        }

        return ResponseEntity
                .ok(map.values());
    }

    /**
     * <h1>종목 시세 스냅샷(사용자가 직접 개별 명시)</h1>
     * @param markets (ex. KRW-BTC,KRW-ETH,BTC-XRP,USDT-XRP,...),
     *                <br>찾지 못한 종목은 생략(예외 발생 x),
     *                <br>최대 개수 제한?
     */
    @GetMapping("/ticker/quotes")
    public ResponseEntity<List<TickerQuotes>> getQuotesSnapshotsOf(@RequestParam("markets") List<String> markets) {

        if (markets == null || markets.size() == 0) {
            throw new BadRequestException(HttpStatus.BAD_REQUEST, "markets 파라미터가 null 이거나 공백입니다.");
        }

        List<TickerQuotes> ret = new ArrayList<>();
        for (String marketCode : markets) {
            int separatorIdx = marketCode.indexOf('-');
            if (separatorIdx <= 0 || marketCode.length() - 1 <= separatorIdx) {
                // 올바르지 않은 종목 코드 형식: 구분자('-') 가 없거나, 첫 문자거나, 마지막 문자인 경우
                throw new BadRequestException(HttpStatus.BAD_REQUEST, "종목 코드는 KRW-BTC 와 같은 형식이어야 합니다. 거부됨: " + marketCode);
            }
            upbitTicker.getQuotesSnapshot(marketCode).ifPresent(ret::add);
        }

        return ResponseEntity
                .ok(ret);
    }

    /**
     * <h1>종목 통계량 중 24시간누적량 스냅샷(호가통화별)</h1>
     */
    @GetMapping("/ticker/acc24h/{quoteCurrencyCode}")
    @JsonView(TickerStatisticsView.Acc24HOnly.class)
    public ResponseEntity<Collection<TickerStatistics>> getStatisticsSnapshotsOfMarketsWhich(
            @PathVariable("quoteCurrencyCode") String quoteCurrencyCode) {

        Map<String, TickerStatistics> map
                = upbitTicker.getStatisticsMapWhichQuoteCurrencyCodeIs(quoteCurrencyCode);

        if (map == null) {
            throw new BadRequestException(
                    HttpStatus.NOT_FOUND,
                    quoteCurrencyCode + " 를 호가통화로 가지는 종목에 대한 통계량 Map 을 찾을 수 없습니다.");
        }

        return ResponseEntity
                .ok(map.values());
    }

    /**
     * <h1>종목 통계량 중 24시간누적량 스냅샷(사용자가 직접 개별 명시)</h1>
     * @param markets (ex. KRW-BTC,KRW-ETH,BTC-XRP,USDT-XRP,...),
     *                <br>찾지 못한 종목은 생략(예외 발생 x),
     *                <br>최대 개수 제한?
     */
    @GetMapping("/ticker/acc24h")
    @JsonView(TickerStatisticsView.Acc24HOnly.class)
    public ResponseEntity<List<TickerStatistics>> getStatisticsSnapshotsOf(@RequestParam("markets") List<String> markets) {

        if (markets == null || markets.size() == 0) {
            throw new BadRequestException(HttpStatus.BAD_REQUEST, "markets 파라미터가 null 이거나 공백입니다.");
        }

        List<TickerStatistics> ret = new ArrayList<>();
        for (String marketCode : markets) {
            int separatorIdx = marketCode.indexOf('-');
            if (separatorIdx <= 0 || marketCode.length() - 1 <= separatorIdx) {
                // 올바르지 않은 종목 코드 형식: 구분자('-') 가 없거나, 첫 문자거나, 마지막 문자인 경우
                throw new BadRequestException(HttpStatus.BAD_REQUEST, "종목 코드는 KRW-BTC 와 같은 형식이어야 합니다. 거부됨: " + marketCode);
            }
            upbitTicker.getStatisticsSnapshot(marketCode).ifPresent(ret::add);
        }

        return ResponseEntity
                .ok(ret);
    }

    /**
     * <h1>
     *     단일 종목 통계량 스냅샷
     * </h1>
     * @param marketCode {호가통화코드}-{기준통화코드} 형식의 문자열 (ex. KRW-BTC), 반드시 대문자여야 함
     */
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
