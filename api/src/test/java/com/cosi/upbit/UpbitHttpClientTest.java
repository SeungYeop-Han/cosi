package com.cosi.upbit;

import com.cosi.upbit.dto.MarketInfo;
import com.cosi.upbit.httpclient.UpbitHttpClient;
import com.cosi.upbit.mirror.UpbitMarkets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClient;

@SpringBootTest
public class UpbitHttpClientTests {

    @Autowired
    RestClient restClient;
    @Autowired
    UpbitHttpClient upbitHttpClient;
    @Autowired
    UpbitMarkets upbitMarkets;

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    @Test
    void contextLoads() {
    }

    @Test
    void checkRestClient() {

        // given
        String uriString = "https://crix-static.upbit.com/crix_master";

        // when
        String responseBodyAsString = restClient
                .get()
                .uri(uriString)
                .retrieve()
                .body(String.class);

        // then
        System.out.println(" >>> " + responseBodyAsString);
    }

    @Test
    void checkUpbitHttpClient() {

        // given
        // when
        List<MarketInfo> marketInfoList = upbitHttpClient.getMarketInfoList();

        //then
        System.out.println(" >>> " + gson.toJson(marketInfoList));
    }

    @Test
    void checkUpbitMarkets() {

        // given
        //when
        List<MarketInfo> upbitMarketsAll = upbitMarkets.findAll();

        //then
        System.out.println(gson.toJson(upbitMarketsAll));
    }
}
