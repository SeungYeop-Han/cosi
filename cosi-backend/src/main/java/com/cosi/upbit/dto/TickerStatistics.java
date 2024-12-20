package com.cosi.upbit.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

/**
 * 가상화폐 종목의 통계량을 나타냅니다.<br>
 * 일정 기간 동안의 누적량이나, 최대값, 주요 지점 에서의 가격 등을 포함합니다.
 */
@Getter
public class TickerStatistics {

    @SerializedName("cd")
    private String code;                        // 마켓 코드, (ex. KRW-BTC)

    @SerializedName("op")
    private double openingPrice;                // 당일 시가
    @SerializedName("hp")
    private double highPrice;                   // 당일 고가
    @SerializedName("lp")
    private double lowPrice;                    // 당일 저가
    @SerializedName("pcp")
    private double prevClosingPrice;            // 전일 종가
    @SerializedName("atv24h")
    private double accTradeVolume24H;           // 24시간 누적 거래량
    @SerializedName("atp24h")
    private double accTradePrice24H;            // 24 시간 누적 거래대금
    @SerializedName("aav")
    private double accAskVolume;                // 누적 매도량
    @SerializedName("abv")
    private double accBidVolume;                // 누적 매수량
    @SerializedName("atv")
    private double accTradeVolume;              // 금일 누적 거래량 (UTC 0시 기준)
    @SerializedName("atp")
    private double accTradePrice;               // 금일 누적 거래대금 (UTC 0시 기준)
    @SerializedName("h52wp")
    private double highest52WeekPrice;          // 52주 최고가
    @SerializedName("h52wdt")
    private String highest52WeekDate;           // 52주 최고가 달성일, yyyy-MM-dd
    @SerializedName("l52wp")
    private double lowest52WeekPrice;           // 52주 최저가
    @SerializedName("l52wdt")
    private String lowest52WeekDate;            // 52주 최저가 달성일, yyyy-MM-dd
    @SerializedName("ms")
    private MarketState marketState;            // 거래 상태, PREVIEW | ACTIVE | DELISTED | PREDELISTED
    @SerializedName("mw")
    private MarketWarning marketWarning;        // 유의 종목 여부, NONE | CAUTION

    /**
     * 종목의 유의 여부를 나타냅니다. 업비트 API 에서 "유의" 와 "주의"는 다른데, "주의"는 API 에서 지원하지 않습니다.
     * 따라서 NONE 과 CAUTION 두 필드만 존재합니다. 추후 "주의" 사항이 지원되면 추가되는 경우 열거형에 추가될 수 있습니다.
     */
    public enum MarketWarning {
        NONE, CAUTION
    }

    // 미사용 필드
//    @SerializedName("tv")
//    private double tradeVolume;                 // 가장 최근 거래량
//    @SerializedName("cp")
//    private double changePrice;                 // 부호 없는 전일 대비 값
//    @SerializedName("cr")
//    private double changeRate;                  // 부호 없는 전일 대비 등락율
//    @SerializedName("tdt")
//    private String tradeDateUtc;                // 최근 거래 일자(UTC), yyyyMMdd
//    @SerializedName("ttm")
//    private String tradeTimeUtc;                // 최근 거래 시각(UTC), HHmmss
//    @SerializedName("ttms")
//    private long tradeTimestamp;                // 체결 타임스탬프 (milliseconds)
//    @SerializedName("ty")
//    private String type;                        // ticker | trade | orderbook
//    @SerializedName("tms")
//    private long timestamp;                     // 타임스탬프 (millisecond)
//    @SerializedName("st")
//    private String streamType;                  // 스트림 타입, SNAPSHOT | REALTIME
//    @SerializedName("dd")
//    private String delistingDate;               // 거래지원 종료일
}
