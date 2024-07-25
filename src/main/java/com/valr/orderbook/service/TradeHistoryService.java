package com.valr.orderbook.service;

import com.valr.orderbook.model.Order;
import com.valr.orderbook.model.Trade;
import com.valr.orderbook.model.TradeHistory;
import com.valr.orderbook.repository.TradeHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TradeHistoryService {

    private final TradeHistoryRepository tradeHistoryRepository;

    @Autowired
    public TradeHistoryService(TradeHistoryRepository tradeHistoryRepository) {
        this.tradeHistoryRepository = tradeHistoryRepository;
    }

    public void addTradeOrder(Order order) {
        tradeHistoryRepository.addTrade(new Trade(order, tradeHistoryRepository.getNextAvailableId()));
    }

    public TradeHistory getTradeHistoryBy(String currencyPair, int skipSize, int limitSize) {
        return tradeHistoryRepository.filterTradeHistoryBy(currencyPair.toUpperCase(), skipSize, limitSize);
    }
}