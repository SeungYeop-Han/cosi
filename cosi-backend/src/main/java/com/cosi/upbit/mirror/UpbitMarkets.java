package com.cosi.upbit.mirror;

import com.cosi.upbit.dto.MarketInfo;
import jakarta.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

/**
 * 업비트 거래소에서 거래 중인 종목 개요 리스트를 제공합니다.<br>
 * <ul>
 * <li/> 구현체에 따라 특정 종목은 필터링 될 수 있습니다.
 * <li/> 구현체에 따라 최신화 주기 또는 시점이 다를 수 있습니다.
 */
public interface UpbitMarkets {

    /**
     * @param baseMarketCode 기준 통화(거래 대상 코인) 코드
     * @param quoteMarketCode 호가 통화(대상 코인의 거래에 사용되는 화폐(ex. KRW, USDT, BTC)) 코드
     * @return 해당하는 종목를 찾지 못한 경우 비어있는 Optional 객체가 반환될 수 있습니다.
     */
    Optional<MarketInfo> find(@Nonnull String baseCurrencyCode, @Nonnull String quoteCurrencyCode);

    /**
     * @return 전체 종목 리스트
     */
    @Nonnull
    List<MarketInfo> findAll();

    /**
     * @return gzip 압축된 종목 리스트 JSON 문자열을 반환합니다.
     */
    byte[] getGzipCompressedMarketListJson();

    /**
     * @return 종목 리스트 버전을 나타내는 etag 반환
     */
    String getEtag();
}
