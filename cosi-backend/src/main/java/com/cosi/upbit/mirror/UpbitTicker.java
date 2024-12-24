package com.cosi.upbit.mirror;

import com.cosi.upbit.dto.TickerQuotes;
import com.cosi.upbit.dto.TickerStatistics;
import java.util.Map;
import java.util.Optional;

/**
 * 업비트에서 거래 중인 종목의 시세 정보를 제공합니다.
 */
public interface UpbitTicker {

    /**
     * 중목 시세 정보를 갱신합니다.
     * @param tickerQuotes
     */
    void updateQuotes(TickerQuotes tickerQuotes);

    /**
     * 종목 통계량 정보를 갱신합니다.
     * @param tickerStatistics
     */
    void updateStatistics(TickerStatistics tickerStatistics);

    /**
     * @return 전 종목의 통계량을 저장하는 Map 을 반환합니다. <br><br>
     * 이 Map 은 종목의 id 를 키로, 통계량 들을 값으로 가집니다. <br><br>
     * 여기서 통계량은 주요 시점의 가격이나, 최대값, 또는 일정 기간동안의 누적량 등, 종목의 거래 현황을 한 눈에 보여주는 값을 말합니다.
     * 이 값들은 그렇게 자주 변하지 않기 때문에 일정 기간마다 주기적으로 업데이트 됩니다.
     */
    Map<String, TickerStatistics> getStatistics();

    /**
     * @param marketCode {호가 통화 코드}-{기준 통화 코드} (ex. KRW-BTC)
     * @return 해당하는 종목의 통계 정보 스냅 샷을 반환합니다. 만약 해당하는 종목이 없다면, 비어있는 Optional 을 반환합니다.
     */
    Optional<TickerStatistics> getStatisticsSnapshot(String marketCode);

    /**
     * @return 전 종목의 실시간 시세 정보를 저장하는 Map 을 반환합니다. <br><br>
     * 이 Map 은 종목의 id 를 키로, 실시간 시세 정보를 값으로 가집니다. <br><br>
     * 여기서 실시간 시세는 가장 최근에 일어난 거래에 의해 결정된 현재가 또는 직전 현재가와 비교했을 때와의 차이 등을 말합니다.
     * 이 값은 수시로 변하기 때문에 실시간으로 업데이트 되어야 됩니다.
     */
    Map<String, TickerQuotes> getQuotes();

    /**
     * @param marketCode {호가 통화 코드}-{기준 통화 코드} (ex. KRW-BTC)
     * @return 해당하는 종목의 시세 정보 스냅 샷을 반환합니다. 만약 해당하는 종목이 없다면, 비어있는 Optional 을 반환합니다.
     */
    Optional<TickerQuotes> getQuotesSnapshot(String marketCode);
}
