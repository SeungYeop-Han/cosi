package com.cosi.upbit.mirror;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class UpbitOrderbookImpl implements UpbitOrderbook {

    Map<String, String> orderbookMap = new HashMap<>();

    @Override
    public void updateOrderbook(String marketCode, String orderbookJsonMessage) {

        orderbookMap.put(marketCode, orderbookJsonMessage);
    }

    @Override
    public Map<String, String> getOrderbooks() {
        return Collections.unmodifiableMap(orderbookMap);
    }

    @Override
    public Optional<String> getOrderbookJsonMessage(String marketCode) {
        return Optional.of(orderbookMap.get(marketCode));
    }
}
