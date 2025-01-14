package com.cosi.upbit.mirror;

import com.cosi.upbit.dto.TickerQuotes;
import com.cosi.upbit.dto.TickerStatistics;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class UpbitTickerImpl implements UpbitTicker {

    private final Map<String, Map<String, TickerQuotes>> mapOfQuotesMaps = Map.of(
            "KRW", new HashMap<>(),
            "BTC", new HashMap<>(),
            "USDT", new HashMap<>()
    );

    private final Map<String, Map<String, TickerStatistics>> mapOfStatisticsMaps = Map.of(
            "KRW", new HashMap<>(),
            "BTC", new HashMap<>(),
            "USDT", new HashMap<>()
    );

    /**
     * 종목 시세 업데이트
     * @param tickerQuotes 특정 종목의 시세 정보
     */
    @Override
    public void updateQuotes(TickerQuotes tickerQuotes) {
        if (tickerQuotes == null) {
            throw new NullPointerException("tickerQuotes 가 null 입니다.");
        }

        mapOfQuotesMaps
                .get(extractQuoteCurrencyCode(tickerQuotes.getCode()))
                .put(tickerQuotes.getCode(), tickerQuotes);
    }

    /**
     * 종목 통계량 정보 업데이트
     * @param tickerStatistics 특정 종목의 통계량 정보
     */
    @Override
    public void updateStatistics(TickerStatistics tickerStatistics) {
        if (tickerStatistics == null) {
            throw new NullPointerException("tickerStatistics 가 null 입니다.");
        }

        mapOfStatisticsMaps
                .get(extractQuoteCurrencyCode(tickerStatistics.getCode()))
                .put(tickerStatistics.getCode(), tickerStatistics);
    }

    /**
     * @param quoteCurrencyCode 호가통화코드 (ex. KRW, BTC, USDT)
     * @return 호가통화코드에 해당하는 종목 통계량 Map
     */
    @Override
    public Map<String, TickerStatistics> getStatisticsMapWhichQuoteCurrencyCodeIs(String quoteCurrencyCode) {
        var found = mapOfStatisticsMaps.get(quoteCurrencyCode);
        if (found == null) {
            return null;
        }
        return Collections.unmodifiableMap(found);
    }

    /**
     * @param marketCode {호가 통화 코드}-{기준 통화 코드} (ex. KRW-BTC)
     * @return 특정 종목의 통계량 스냅샷 정보
     */
    @Override
    public Optional<TickerStatistics> getStatisticsSnapshot(String marketCode) {

        Map<String, TickerStatistics> targetMap = mapOfStatisticsMaps
                .get(extractQuoteCurrencyCode(marketCode));

        if (targetMap == null) {
            Optional.ofNullable(null);
        }

        TickerStatistics found = targetMap.get(marketCode);

        return Optional.ofNullable(found);
    }

    /**
     * @param quoteCurrencyCode 호가통화코드 (ex. KRW, BTC, USDT)
     * @return 호가통화코드에 해당하는 종목 시세 Map
     */
    @Override
    public Map<String, TickerQuotes> getQuotesMapWhichQuoteCurrencyCodeIs(String quoteCurrencyCode) {
        var found = mapOfQuotesMaps.get(quoteCurrencyCode);
        if (found == null) {
            return null;
        }
        return Collections.unmodifiableMap(found);
    }

    /**
     * @param marketCode {호가 통화 코드}-{기준 통화 코드} (ex. KRW-BTC)
     * @return 특정 종목의 시세 스냅샷 정보
     */
    @Override
    public Optional<TickerQuotes> getQuotesSnapshot(String marketCode) {

        Map<String, TickerQuotes> targetMap = mapOfQuotesMaps
                .get(extractQuoteCurrencyCode(marketCode));

        if (targetMap == null) {
            Optional.ofNullable(null);
        }

        TickerQuotes found = targetMap.get(marketCode);

        return Optional.ofNullable(found);
    }

    /**
     * @param marketCode {호가통화코드}-{기준통화코드} 형식의 종목 코드 (ex. KRW-BTC)
     * @return 호가통화코드를 반환한다.
     */
    private String extractQuoteCurrencyCode(String marketCode) {
        return marketCode.substring(0, marketCode.indexOf('-'));
    }
}
