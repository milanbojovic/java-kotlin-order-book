package com.valr.orderbook.model;

import com.valr.orderbook.model.enumeration.Side;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class Trade {
    private int id;
    private int price;
    private double quantity;
    private String currencyPair;
    private String tradedAt;
    private Side takerSide;
    private double quoteVolume;

    public Trade(int id, int price, double quantity, String currencyPair, String tradedAt, Side takerSide, double quoteVolume) {
        this.id = id;
        this.price = price;
        this.quantity = quantity;
        this.currencyPair = currencyPair;
        this.tradedAt = tradedAt;
        this.takerSide = takerSide;
        this.quoteVolume = quoteVolume;
    }

    public Trade(Order order, int id) {
        this.id = id;
        this.price = order.getPrice();
        this.quantity = order.getQuantity();
        this.currencyPair = order.getCurrencyPair();
        this.tradedAt = Instant.now().toString();
        this.takerSide = order.getSide();
        this.quoteVolume = order.getPrice() * order.getQuantity();
    }
}