package com.cosi.upbit.mirror;

import com.cosi.upbit.dto.TickerRealtimeQuotes;
import com.cosi.upbit.dto.TickerStatistics;
import java.util.Map;

/**
 * 업비트에서 거래 중인 종목의 시세 정보를 제공합니다.
 */
public interface UpbitTicker {

    /**
     * @return 전 종목의 통계량을 저장하는 Map 을 반환합니다. <br><br>
     * 이 Map 은 종목의 id 를 키로, 통계량 들을 값으로 가집니다. <br><br>
     * 여기서 통계량은 주요 시점의 가격이나, 최대값, 또는 일정 기간동안의 누적량 등, 종목의 거래 현황을 한 눈에 보여주는 값을 말합니다.
     * 이 값들은 그렇게 자주 변하지 않기 때문에 일정 기간마다 주기적으로 업데이트 됩니다.
     */
    Map<String, TickerStatistics> getStatistics();

    /**
     * @return 전 종목의 실시간 시세 정보를 저장하는 Map 을 반환합니다. <br><br>
     * 이 Map 은 종목의 id 를 키로, 실시간 시세 정보를 값으로 가집니다. <br><br>
     * 여기서 실시간 시세는 가장 최근에 일어난 거래에 의해 결정된 현재가 또는 직전 현재가와 비교했을 때와의 차이 등을 말합니다.
     * 이 값은 수시로 변하기 때문에 실시간으로 업데이트 되어야 됩니다.
     */
    Map<String, TickerRealtimeQuotes> getRealtimeQuotes();
}
