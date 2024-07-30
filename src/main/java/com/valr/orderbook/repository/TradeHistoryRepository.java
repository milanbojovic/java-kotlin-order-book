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

import static com.valr.orderbook.util.CurrencyPairConstants.*;

/**
 * Repository class for managing trade history.
 */
@Component
@Data
public class TradeHistoryRepository {
    TradeHistory tradeHistory;

    /**
     * Constructor for TradeHistoryRepository.
     * Initializes the trade history.
     */
    public TradeHistoryRepository() {
        tradeHistory = TradeHistory.builder().build();
    }

    /**
     * Filters the trade history by the specified currency pair, skipping a number of records and limiting the result.
     *
     * @param currencyPair the currency pair to filter by
     * @param skip the number of records to skip
     * @param limit the maximum number of records to return
     * @return a filtered TradeHistory object
     */
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

    /**
     * Adds a trade to the trade history.
     *
     * @param trade the trade to add
     */
    public void addTrade(Trade trade) {
        tradeHistory.addTrade(trade);
    }

    /**
     * Gets the next available ID for a new trade.
     *
     * @return the next available ID
     */
    public int getNextAvailableId() {
        return tradeHistory.getTrades().stream()
                .map(Trade::getId)
                .max(Integer::compareTo)
                .map(num -> num + 1)
                .orElse(0);
    }

    /**
     * Adds initial data to the repository on startup.
     * This is just for easier presentation purposes; for a live system, initialization would be added in unit tests.
     */
    @PostConstruct
    public void insertData() {
        createExampleTradesList(tradeHistory);
    }

    /**
     * Creates an example list of trades and adds it to the trade history.
     *
     * @param tradeHistory the trade history to add the example trades to
     */
    private void createExampleTradesList(TradeHistory tradeHistory) {
        List<Trade> trades = new LinkedList<>();
        tradeHistory.setTrades(trades);
        trades.add(new Trade(getNextAvailableId(),1199677, 0.00213752, BTC_EUR, "2024-07-11T08:50:12.453Z", Side.SELL, 2564.33358104));
        trades.add(new Trade(getNextAvailableId(),1200677, 0.03225700, BTC_USD, "2024-08-10T09:22:15.363Z", Side.SELL, 38730.237989));
        trades.add(new Trade(getNextAvailableId(),1230650, 0.00456120, ETH_ZAR, "2024-09-15T18:32:16.363Z", Side.SELL, 5613.24078));
        trades.add(new Trade(getNextAvailableId(),1358400, 0.75689132, ETH_EUR, "2024-10-17T14:22:18.433Z", Side.SELL, 1028161.169088));
        trades.add(new Trade(getNextAvailableId(),1005522, 2.56879135, ETH_USD, "2024-11-19T03:52:17.413Z", Side.SELL, 2582976.2158347));
        trades.add(new Trade(getNextAvailableId(),1015459, 0.56879135, BTC_ZAR, "2022-10-11T13:44:24.571Z", Side.SELL, 570680.8748647));
        trades.add(new Trade(getNextAvailableId(),5168975, 0.56879135, BTC_ZAR, "2022-10-11T13:44:24.571Z", Side.SELL, 570680.8748647));
        trades.add(new Trade(getNextAvailableId(),2159877, 0.56879135, BTC_ZAR, "2022-10-11T13:44:24.571Z", Side.SELL, 570680.8748647));
        trades.add(new Trade(getNextAvailableId(),1111115, 0.56879135, BTC_ZAR, "2022-10-11T13:44:24.571Z", Side.SELL, 570680.8748647));
        trades.add(new Trade(getNextAvailableId(),2222222, 0.56879135, BTC_ZAR, "2022-10-11T13:44:24.571Z", Side.SELL, 570680.8748647));
        trades.add(new Trade(getNextAvailableId(),4567895, 0.56879135, BTC_ZAR, "2022-10-11T13:44:24.571Z", Side.SELL, 570680.8748647));
    }
}