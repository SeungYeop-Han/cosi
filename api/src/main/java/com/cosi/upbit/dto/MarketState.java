package com.cosi.upbit.dto;

/**
 * 가상 화폐 종목의 거래 상태를 의미합니다.
 */
public enum MarketState {
    /**
     * 상장 예정
     */
    PREVIEW,
    /**
     * 거래 가능
     */
    ACTIVE,
    /**
     * 상장 폐지
     */
    DELISTED,
    /**
     * 상장 폐지 예정
     */
    PREDELISTING;
}
