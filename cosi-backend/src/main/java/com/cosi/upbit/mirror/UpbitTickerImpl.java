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

    private final Map<String, TickerQuotes> quotesMap = new HashMap<>();
    private final Map<String, TickerStatistics> statisticsMap = new HashMap<>();

    @Override
    public void updateQuotes(TickerQuotes tickerQuotes) {
        if (tickerQuotes == null) {
            throw new NullPointerException("tickerQuotes 가 null 입니다.");
        }
        quotesMap.put(tickerQuotes.getCode(), tickerQuotes);
    }

    @Override
    public void updateStatistics(TickerStatistics tickerStatistics) {
        if (tickerStatistics == null) {
            throw new NullPointerException("tickerStatistics 가 null 입니다.");
        }
        statisticsMap.put(tickerStatistics.getCode(), tickerStatistics);
    }

    @Override
    public Map<String, TickerStatistics> getStatistics() {
        return Collections.unmodifiableMap(statisticsMap);
    }

    @Override
    public Optional<TickerStatistics> getStatisticsSnapshot(String marketCode) {
        return Optional.ofNullable(statisticsMap.get(marketCode));
    }

    @Override
    public Map<String, TickerQuotes> getQuotes() {
        return Collections.unmodifiableMap(quotesMap);
    }

    @Override
    public Optional<TickerQuotes> getQuotesSnapshot(String marketCode) {
        return Optional.ofNullable(quotesMap.get(marketCode));
    }
}
