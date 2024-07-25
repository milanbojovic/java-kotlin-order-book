package com.valr.orderbook.repository;

import com.valr.orderbook.model.Trade;
import com.valr.orderbook.model.TradeHistory;
import com.valr.orderbook.model.enumeration.Side;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Component
@Data
public class TradeHistoryRepository {
    TradeHistory tradeHistory;

    public TradeHistoryRepository() {
        tradeHistory = TradeHistory.builder().build();
    }

    public TradeHistory filterTradeHistoryBy(String currencyPair, int skip, int limit) {
        return TradeHistory.builder()
                .trades(Optional.ofNullable(tradeHistory.getTrades()).orElse(Collections.emptyList())
                        .stream()
                        .filter(trade -> trade.getCurrencyPair().equals(currencyPair))
                        .skip(skip)
                        .limit(limit)
                        .toList())
                .build();
    }

    public void addTrade(Trade trade) {
        tradeHistory.addTrade(trade);
    }

    public int getNextAvailableId() {
        return tradeHistory.getTrades().stream()
                .map(Trade::getId)
                .max(Integer::compareTo)
                .map(num -> num + 1)
                .orElse(0);
    }

    @PostConstruct
    public void insertData() {
        createExampleTradesList(tradeHistory);
    }

    private List<Trade> createExampleTradesList(TradeHistory tradeHistory) {
        List<Trade> trades = new LinkedList<>();
        tradeHistory.setTrades(trades);
        trades.add(new Trade(getNextAvailableId(),1199677, 0.00213752, "BTCEUR", "2024-07-11T08:50:12.453Z", Side.SELL, 2564.33358104));
        trades.add(new Trade(getNextAvailableId(),1200677, 0.03225700, "BTCUSD", "2024-08-10T09:22:15.363Z", Side.SELL, 38730.237989));
        trades.add(new Trade(getNextAvailableId(),1230650, 0.00456120, "ETHZAR", "2024-09-15T18:32:16.363Z", Side.SELL, 5613.24078));
        trades.add(new Trade(getNextAvailableId(),1358400, 0.75689132, "ETHEUR", "2024-10-17T14:22:18.433Z", Side.SELL, 1028161.169088));
        trades.add(new Trade(getNextAvailableId(),1005522, 2.56879135, "ETHUSD", "2024-11-19T03:52:17.413Z", Side.SELL, 2582976.2158347));
        trades.add(new Trade(getNextAvailableId(),1015459, 0.56879135, "BTCZAR", "2022-10-11T13:44:24.571Z", Side.SELL, 570680.8748647));
        trades.add(new Trade(getNextAvailableId(),5168975, 0.56879135, "BTCZAR", "2022-10-11T13:44:24.571Z", Side.SELL, 570680.8748647));
        trades.add(new Trade(getNextAvailableId(),2159877, 0.56879135, "BTCZAR", "2022-10-11T13:44:24.571Z", Side.SELL, 570680.8748647));
        trades.add(new Trade(getNextAvailableId(),1111115, 0.56879135, "BTCZAR", "2022-10-11T13:44:24.571Z", Side.SELL, 570680.8748647));
        trades.add(new Trade(getNextAvailableId(),2222222, 0.56879135, "BTCZAR", "2022-10-11T13:44:24.571Z", Side.SELL, 570680.8748647));
        trades.add(new Trade(getNextAvailableId(),4567895, 0.56879135, "BTCZAR", "2022-10-11T13:44:24.571Z", Side.SELL, 570680.8748647));
        return trades;
    }
}
