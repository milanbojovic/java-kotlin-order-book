package com.valr.orderbook.util;

import com.valr.orderbook.model.Order;
import com.valr.orderbook.model.OrderBook;
import com.valr.orderbook.model.enumeration.Side;

import java.util.Arrays;
import java.util.List;

public class TestHelper {

    public static OrderBook createOrderBook() {
        return OrderBook.builder()
                .bids(createOrdersList(Side.BUY))
                .asks(createOrdersList(Side.SELL))
                .build();
    }

    public static List<Order> createOrdersList(Side side) {
        return Arrays.asList(
                new Order(side, 1.0, 1, "BTCZAR"),
                new Order(side, 2.0, 2, "BTCEUR"),
                new Order(side, 3.0, 3, "BTCZAR")
        );
    }

    public static Order createOrder(Side side, double quantity, int price, String currencyPair) {
        return Order.builder()
                .side(side)
                .quantity(quantity)
                .price(price)
                .currencyPair(currencyPair)
                .build();
    }
}
