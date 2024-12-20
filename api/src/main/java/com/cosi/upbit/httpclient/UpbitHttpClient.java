package com.cosi.upbit.httpclient;

import com.cosi.upbit.dto.MarketInfo;
import java.util.List;

/**
 * <pre>
 * 업비트 HTTP API 에 대한 각 요청을 나타냅니다.
 *
 * [업비트 Quotation API - REST]
 *
 * - 공식 API URL: http://api.upbit.com/v1 *
 * - 공식 API 요청 제한 횟수: 초당 10회
 *      1. 종목, 캔들, 체결, 티커, 호가별로 각각 적용
 *      2. IP 단위로 측정
 *      2. 잔여 요청 횟수는 "Remaining-Req" 헤더를 통해 확인 가능
 *      3. 해당 시간 내 초과된 요청에 대해서 429(Too Many Request) 에러가 발생할 수 있음
 *
 * - 비공식 CRIX API URL: https://crix-api-endpoint.upbit.com/v1/crix
 * - 비공식 API는 더 자유롭게 사용할 수 있음.
 *
 * - 업비트 API 공식 문서
 *      1. <a href="https://docs.upbit.com/docs/upbit-quotation-restful-api">REST API를 이용한 업비트 시세 수신</a>
 *      2. <a href="https://docs.upbit.com/reference">API 레퍼런스</a>
 *
 * - 업비트 비공식 API 정리
 *      1. <a href="https://ammezkhan.notion.site/API-122c43d953e8805899b7f93fe1f9a791?pvs=4">업비트 비공식 API 정리</a>
 * </pre>
 */
public interface UpbitHttpClient {

    /**
     * <pre>
     * [종목 코드 조회]
     * 경로 : <a href="https://crix-static.upbit.com/crix_master">https://crix-static.upbit.com/crix_master</a>
     * 참조 : <a href="https://docs.upbit.com/reference/%EB%A7%88%EC%BC%93-%EC%BD%94%EB%93%9C-%EC%A1%B0%ED%9A%8C">종목 코드 조회</a>
     * ※ 주의 - 응답 메시지 본문이 gzip 압축되어 있음
     * </pre>
     *
     * @return @return {@link List}&lt;{@link MarketInfo}&gt;
     */
    List<MarketInfo> getMarketInfoList();
}
