package com.cosi.upbit.mirror;

import java.util.Map;
import java.util.Optional;

/**
 * 업비트에서 거래 중인 종목의 호가 정보를 제공합니다.
 */
public interface UpbitOrderbook {

    /**
     * 종목 호가 정보를 갱신합니다.
     * @param marketCode 대상 종목 코드
     * @param orderbookJsonMessage 문자열
     */
    void updateOrderbook(String marketCode, String orderbookJsonMessage);

    /**
     * @return 전 종목의 실시간 호가 정보 리스트
     */
    Map<String, String> getOrderbooks();

    /**
     * @param marketCode 대상 종목 코드
     * @return 해당 종목의 실시간 호가 정보 문자열
     */
    Optional<String> getOrderbookJsonMessage(String marketCode);
}
