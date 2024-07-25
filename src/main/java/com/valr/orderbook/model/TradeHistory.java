package com.valr.orderbook.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class TradeHistory {
    private List<Trade> trades;

    public TradeHistory() {
        trades = new LinkedList<>();
    }

    public void addTrade(Trade trade) {
        trades.add(trade);
    }
}