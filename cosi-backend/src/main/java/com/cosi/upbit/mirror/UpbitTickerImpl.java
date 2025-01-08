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

    @Override
    public void updateQuotes(TickerQuotes tickerQuotes) {
        if (tickerQuotes == null) {
            throw new NullPointerException("tickerQuotes 가 null 입니다.");
        }

        mapOfQuotesMaps
                .get(extractQuoteCurrencyCode(tickerQuotes.getCode()))
                .put(tickerQuotes.getCode(), tickerQuotes);
    }

    @Override
    public void updateStatistics(TickerStatistics tickerStatistics) {
        if (tickerStatistics == null) {
            throw new NullPointerException("tickerStatistics 가 null 입니다.");
        }

        mapOfStatisticsMaps
                .get(extractQuoteCurrencyCode(tickerStatistics.getCode()))
                .put(tickerStatistics.getCode(), tickerStatistics);
    }

    @Override
    public Map<String, TickerStatistics> getStatisticsMapWhichQuoteCurrencyCodeIs(String quoteCurrencyCode) {
        var found = mapOfStatisticsMaps.get(quoteCurrencyCode);
        if (found == null) {
            throw new NullPointerException("호가통화코드 " + quoteCurrencyCode + "에 대한 통계량 Map 을 찾을 수 없습니다.");
        }
        return Collections.unmodifiableMap(found);
    }

    @Override
    public Optional<TickerStatistics> getStatisticsSnapshot(String marketCode) {
        return Optional.ofNullable(
                mapOfStatisticsMaps
                        .get(extractQuoteCurrencyCode(marketCode))
                        .get(marketCode)
        );
    }

    @Override
    public Map<String, TickerQuotes> getQuotesMapWhichQuoteCurrencyCodeIs(String quoteCurrencyCode) {
        var found = mapOfQuotesMaps.get(quoteCurrencyCode);
        if (found == null) {
            throw new NullPointerException("호가통화코드 " + quoteCurrencyCode + "에 대한 시세 Map 을 찾을 수 없습니다.");
        }
        return Collections.unmodifiableMap(found);
    }

    @Override
    public Optional<TickerQuotes> getQuotesSnapshot(String marketCode) {
        return Optional.ofNullable(
                mapOfQuotesMaps
                        .get(extractQuoteCurrencyCode(marketCode))
                        .get(marketCode)
        );
    }

    /**
     * @param marketCode {호가통화코드}-{기준통화코드} 형식의 종목 코드 (ex. KRW-BTC)
     * @return 호가통화코드를 반환한다.
     */
    private String extractQuoteCurrencyCode(String marketCode) {
        return marketCode.substring(0, marketCode.indexOf('-'));
    }
}
