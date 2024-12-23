package com.cosi.upbit.mirror;

import com.cosi.upbit.dto.MarketInfo;
import com.cosi.upbit.httpclient.UpbitHttpClient;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * UpbitHttpClient 를 사용하여 종목 리스트를 확보합니다.<br><br>
 * 종목 리스트는 업비트를 통해 거래되는, 현재 거래 가능한 종목들로 한정됩니다.<br><br>
 * 정해진 시간 간격 내에, 한 번만 종목 리스트를 가져옵니다. 시간 간격은 생성 시 초 단위로 설정할 수 있습니다.
 * 단, 유효 시간이 지나더라도 요청이 발생하기 전 까지는 종목 리스트를 가져오지 않습니다.<br><br>
 * 본 클래스는 {@link com.cosi.upbit.config.UpbitClientConfig} 내에 빈으로 등록되어 있습니다.
 */
public class UpbitMarketsImpl implements UpbitMarkets {

    // 종목 리스트 수신을 위한 업비트 HTTP 클라이언트(주입됨)
    private final UpbitHttpClient upbitHttpClient;

    // 종목 개요 리스트
    private List<MarketInfo> marketInfoList = null;
    // 종목 정보 맵: 빠른 검색을 위해 도입함, MAX_AGE_IN_SECONDS 값이 작다면 오히려 비효율적일 수 있으므로 주의
    private Map<String, MarketInfo> marketDetailsMap = new HashMap<>();
    // 유효 시간(초)
    private final int MAX_AGE_IN_SECONDS;
    // 만료 시각, 해당 시각이 되기 전 까지는 update() 가 호출되도 업비트 API 서버에 요청을 보내지 않고 캐시를 활용 함
    private long expiredAt;  // timestamp

    /**
     * 생성 시 MAX_AGE_IN_SECONDS 값에 무관하게 항상 종목 리스트를 갱신
     * @param upbitHttpClient 종목 리스트 요청에 사용할 업비트 HTTP 클라이언트
     * @param MAX_AGE_IN_SECONDS 캐싱할 시간(초)
     */
    public UpbitMarketsImpl(UpbitHttpClient upbitHttpClient, int MAX_AGE_IN_SECONDS) {

        if (upbitHttpClient == null || MAX_AGE_IN_SECONDS < 1) {
            throw new IllegalArgumentException("upbitHttpClient 가 null 이거나 MAX_AGE 가 1보다 작습니다.");
        }

        this.upbitHttpClient = upbitHttpClient;
        this.MAX_AGE_IN_SECONDS = MAX_AGE_IN_SECONDS;
        this.expiredAt = System.currentTimeMillis();

        update();
    }

    /**
     * 업비트로부터 종목 리스트를 가져온 뒤 자체 규칙에 따라 필터링합니다.
     * 본 클래스는 업비트 거래소에서 거래되는, 현재 거래 가능한 상태의 종목만 저장합니다.
     */
    private void update() {
        marketInfoList = upbitHttpClient.getMarketInfoList().stream()
                .filter(marketDetails -> {
                    // getter 로 값을 직접 받아와서 필터링 하는 구조에서,
                    // MarketDetails 에서 조건 검사 후 결과를 반환하는 식으로 변경
                    // 이는 API 값 형식 등이 변하더라도 MarketDetails 만 변경하면 되도록 만들어주므로 확장성을 늘려준다.
                    return marketDetails.isTradable() && marketDetails.isExchangeUpbit();
                })
                .toList();

        expiredAt += (long) MAX_AGE_IN_SECONDS * 1000;

        for (MarketInfo marketInfo : marketInfoList) {
            if (marketDetailsMap.containsKey(marketInfo.getId())) {
                marketDetailsMap.clear();
                throw new IllegalStateException("종목 리스트로부터 맵을 초기화하는 중 키 충돌이 일어났습니다. 맵을 사용하지 않도록 설정합니다.");
            }
            marketDetailsMap.put(marketInfo.getId(), marketInfo);
        }
    }

    private boolean isCacheExpired() {
        return expiredAt < System.currentTimeMillis();
    }

    @Override
    public Optional<MarketInfo> find(String baseMarketCode, String quoteMarketCode) {

        if (baseMarketCode == null || quoteMarketCode == null) {
            throw new IllegalArgumentException("baseMarketCode 또는 quoteMarketCode 가 null 입니다.");
        }

        if (isCacheExpired()) {
            update();
        }

        // 맵에서 값을 찾아서 반환
        if ( ! marketDetailsMap.isEmpty()) {
            String id = "UPBIT" + quoteMarketCode + baseMarketCode;
            return Optional.of(marketDetailsMap.get(id));
        }

        // 맵을 사용할 수 없는 경우 리스트로부터 값을 검색해서 반환
        for (MarketInfo marketInfo : marketInfoList) {
            if (marketInfo.getBaseCurrencyCode().equals(baseMarketCode)
                    || marketInfo.getQuoteCurrencyCode().equals(quoteMarketCode)) {
                return Optional.ofNullable(marketInfo);
            }
        }

        return Optional.empty();
    }

    @Override
    public List<MarketInfo> findAll() {

        if (isCacheExpired()) {
            update();
        }

        return Collections.unmodifiableList(marketInfoList);
    }
}
