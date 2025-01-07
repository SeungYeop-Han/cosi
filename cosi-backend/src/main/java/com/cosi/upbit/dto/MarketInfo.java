package com.cosi.upbit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 업비트에 상장된 가상화폐 종목(기준통화-호가통화 쌍) 개요 정보
 * <ul>
 * <li/> crixCode: 업비트 종목 식별 코드, CRIX.{exchange}.{quoteCurrencyCode}-{baseCurrencyCode}, (ex. CRIX.UPBIT.KRW-BTC)
 * <li/> exchange: 거래소, (ex. UPBIT, COINMARKETCAP, ...)
 * <li/> koreanName: 국문종목명, (ex. 비트코인, 테더, ...)
 * <li/> englishName: 영문종목명, (ex. Bitcoin, Tether)
 * <li/> baseCurrencyCode: 기준통화코드, (ex. BTC)
 * <li/> quoteCurrencyCode: 호가통화코드, (ex. KRW)
 * <li/> baseCurrencyDecimalPlace: 기준통화 최소 소수점 자리, (ex. 8)
 * <li/> quoteCurrencyDecimalPlace: 호가통화 최소 소수점 자리, (ex. 0)
 * <li/> marketState: 종목 상태, PREVIEW | ACTIVE | DELISTED | PREDELISTED
 * <li/> isTradingSuspended: 거래 중단 여부, true/false
 * <li/> listingDate: 상장일, yyyy-mm-dd
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class MarketInfo {

    // 매핑 필드
    @JsonProperty("exchange")
    private String exchange;                    // 거래소 (ex. UPBIT, COINMARKETCAP, ...)
    @JsonProperty("koreanName")
    private String koreanName;                  // 기준통화국문명 (ex. 비트코인)
    @JsonProperty("englishName")
    private String englishName;                 // 기준통화영문명 (ex. Bitcoin)
    @JsonProperty("pair")
    private String pair;                        // ex. BTC/KRW
    @JsonProperty("baseCurrencyCode")
    private String baseCurrencyCode;            // 기준통화코드 (ex. BTC)
    @JsonProperty("quoteCurrencyCode")
    private String quoteCurrencyCode;           // 호가통화코드 (ex. KRW)
    // 호가통화 = 기준통화의 가치를 측정하기 위해 사용되는 통화
    @JsonProperty("baseCurrencyDecimalPlace")
    private int baseCurrencyDecimalPlace;       // 기준통화 최소 소수점 자리 (ex. 8)
    @JsonProperty("quoteCurrencyDecimalPlace")
    private int quoteCurrencyDecimalPlace;      // 호가통화 최소 소수점 자리 (ex. 0)
    @JsonProperty("marketState")
    private MarketState marketState;            // 상태 (PREVIEW | ACTIVE | DELISTED | PREDELISTED)
    @JsonProperty("listingDate")
    private String listingDate;                 // 상장일 (yyyy-mm-dd)
    @JsonProperty("delistingDate")
    private String delistingDate;            // 상장폐지(예정)일 (yyyy-mm-dd)

    /**
     * @return 거래소가 업비트인지 여부를 반환합니다. 예를 들어 exchange 필드가 COINMARKETCAP 인 경우에는 false 를 반환합니다.
     */
    public boolean isExchangeUpbit() {
        return exchange.equals("UPBIT");
    }

    /**
     * @return 거래가 가능한 상태인지의 여부를 반환합니다. getMarketState() == MarketState.ACTIVE 인 경우에만 true 가 반환됩니다.
     */
    public boolean isTradable() {
        return marketState.equals(MarketState.ACTIVE);
    }

    // 미사용 필드 들
//    @JsonProperty("code")
//    private String crixCode;                    // CRIX.{거래소코드}.{호가통화코드}.{기준통화코드} (ex. CRIX.UPBIT.KRW-BTC)
//    @JsonProperty("localName")
//    private String localName;                // ex. 비트코인
//    @JsonProperty("tradeSupportedMarket")
//    private boolean tradeSupportedMarket;    // ?
//    @JsonProperty("marketStateForIos")
//    private String marketStateForIos;        // 상태 (PREVIEW | ACTIVE | DELISTED | PREDELISTED)
//    @JsonProperty("tradeStatus")
//    private String tradeStatus;              // 상태 (PREVIEW | ACTIVE | DELISTED | PREDELISTED)
//    @JsonProperty("isTradingSuspended")
//    private boolean isTradingSuspended;      // 거래 중단 여부 (true | false)
//    @JsonProperty("timestamp")
//    private long timestamp;                  // epoch time(milli seconds)
}