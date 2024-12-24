package com.cosi.upbit;

import com.cosi.upbit.dto.TickerQuotes;
import com.cosi.upbit.dto.TickerStatistics;
import com.cosi.upbit.mirror.UpbitOrderbook;
import com.cosi.upbit.mirror.UpbitTicker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UpbitWebsocketClientTests {

    @Autowired
    UpbitTicker upbitTicker;

    @Autowired
    UpbitOrderbook upbitOrderbook;

    private final Gson gson = new GsonBuilder()
//            .setPrettyPrinting()
            .create();

    @Test
    void contextLoads() {
    }

    @Test
    void checkUpbitTicker() throws InterruptedException, IOException {

        // given
        Thread.sleep(1000);
        String marketCode = "KRW-BTC";

        // when
        Map<String, TickerQuotes> quotesMap = upbitTicker.getQuotes();
        Map<String, TickerStatistics> statisticsMap = upbitTicker.getStatistics();

        // then
        TickerStatistics statistics = statisticsMap.get(marketCode);
        TickerQuotes quotes = quotesMap.get(marketCode);
        System.out.println("Statistics: " + gson.toJson(statistics));
        System.out.println("Quotes: " + gson.toJson(quotes));

        while (true) {

            TickerStatistics statisticsTemp = statisticsMap.get(marketCode);
            TickerQuotes quotesTemp = quotesMap.get(marketCode);


            if (!gson.toJson(statistics).equals(gson.toJson(statisticsTemp))) {
                statistics = statisticsTemp;
                System.out.println("Statistics: " + gson.toJson(statistics));
            }

            if (!gson.toJson(quotes).equals(gson.toJson(quotesTemp))) {
                quotes = quotesTemp;
                System.out.println("Quotes: " + gson.toJson(quotes));
            }

            Thread.sleep(10);
        }
    }

    @Test
    void checkUpbitOrderbook() throws Exception {

        // given
        String marketCode = "KRW-BTC";

        // when
        String snapshot = upbitOrderbook.getOrderbookJsonMessage(marketCode).orElseThrow(() -> {
            throw new IllegalArgumentException(marketCode + " 를 찾을 수 없습니다.");
        });
        Map<String, String> orderbookMap = upbitOrderbook.getOrderbooks();

        //then
        System.out.println(marketCode + " SNAPSHOT: " + snapshot);
        System.out.println("===== REALTIME =====");
        while (true) {
            System.out.println(orderbookMap.get(marketCode));
            Thread.sleep(100);
        }
    }
}
