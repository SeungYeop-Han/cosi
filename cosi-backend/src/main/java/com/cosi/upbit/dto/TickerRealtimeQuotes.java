package com.cosi.upbit.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

/**
 * 가상화폐 종목의 실시간 시세 정보를 나타냅니다.
 */
@Getter
public class TickerRealtimeQuotes {

    @SerializedName("cd")
    private String code;                        // 마켓 코드, (ex. KRW-BTC)

    @SerializedName("tp")
    private double tradePrice;                  // 현재가
    @SerializedName("c")
    private Change change;                      // 전일 대비, RISE | EVEN | FALL
    @SerializedName("scp")
    private double signedChangePrice;           // 전일 대비 값
    @SerializedName("scr")
    private double signedChangeRate;            // 전일 대비 등락율
    @SerializedName("ab")
    private String askOrBid;                    // 매수/매도 구분, ask: 매도, bid: 매수

    /**
     * 직전 시세와 비교했을 때, 가격이 올랐는지(RISE), 떨어졌는지(FALL), 아니면 유지되었는지(EVEN) 여부를 나타냅니다.
     */
    public enum Change {
        RISE(1), EVEN(0), FALL(-1);

        private int value;
        private Change(int value) {
            this.value = value;
        }
    }
}
