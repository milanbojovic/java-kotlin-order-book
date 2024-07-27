package com.valr.orderbook.service;

import com.valr.orderbook.model.Order;
import com.valr.orderbook.model.Trade;
import com.valr.orderbook.model.TradeHistory;
import com.valr.orderbook.repository.TradeHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class for managing trade history operations.
 */
@Service
public class TradeHistoryService {

    private final TradeHistoryRepository tradeHistoryRepository;

    /**
     * Constructor for TradeHistoryService.
     *
     * @param tradeHistoryRepository the repository for managing trade history data
     */
    @Autowired
    public TradeHistoryService(TradeHistoryRepository tradeHistoryRepository) {
        this.tradeHistoryRepository = tradeHistoryRepository;
    }

    /**
     * Adds a trade order to the trade history.
     *
     * @param order the order to be added as a trade
     */
    public void addTradeOrder(Order order) {
        tradeHistoryRepository.addTrade(new Trade(order, tradeHistoryRepository.getNextAvailableId()));
    }

    /**
     * Retrieves the trade history for a given currency pair with optional filtering.
     *
     * @param currencyPair the currency pair to filter the trade history by
     * @param skipSize the number of records to skip
     * @param limitSize the maximum number of records to return
     * @return the filtered trade history
     */
    public TradeHistory getTradeHistoryBy(String currencyPair, int skipSize, int limitSize) {
        return tradeHistoryRepository.filterTradeHistoryBy(currencyPair.toUpperCase(), skipSize, limitSize);
    }
}