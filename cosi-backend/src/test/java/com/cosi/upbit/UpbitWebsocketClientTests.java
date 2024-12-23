package com.cosi.upbit;

import com.cosi.upbit.dto.TickerRealtimeQuotes;
import com.cosi.upbit.dto.TickerStatistics;
import com.cosi.upbit.mirror.UpbitTicker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UpbitWebsocketClientTests {

    @Autowired
    UpbitTicker upbitTicker;

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
        Map<String, TickerRealtimeQuotes> realtimeQuotesMap = upbitTicker.getRealtimeQuotes();
        Map<String, TickerStatistics> statisticsMap = upbitTicker.getStatistics();

        // then
        TickerStatistics statistics = statisticsMap.get(marketCode);
        TickerRealtimeQuotes realtimeQuotes = realtimeQuotesMap.get(marketCode);
        System.out.println("Statistics: " + gson.toJson(statistics));
        System.out.println("Realtime Quotes: " + gson.toJson(realtimeQuotes));

        while (true) {

            TickerStatistics statisticsTemp = statisticsMap.get(marketCode);
            TickerRealtimeQuotes realtimeQuotesTemp = realtimeQuotesMap.get(marketCode);


            if (!gson.toJson(statistics).equals(gson.toJson(statisticsTemp))) {
                statistics = statisticsTemp;
                System.out.println("Statistics: " + gson.toJson(statistics));
            }

            if (!gson.toJson(realtimeQuotes).equals(gson.toJson(realtimeQuotesTemp))) {
                realtimeQuotes = realtimeQuotesTemp;
                System.out.println("Realtime Quotes: " + gson.toJson(realtimeQuotes));
            }

            Thread.sleep(10);
        }
    }
}
