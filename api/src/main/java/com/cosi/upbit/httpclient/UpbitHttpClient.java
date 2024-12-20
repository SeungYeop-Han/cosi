package com.cosi.upbit.httpclient;

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
}
