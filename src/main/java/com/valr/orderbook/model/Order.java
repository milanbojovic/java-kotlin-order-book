package com.valr.orderbook.model;

import com.valr.orderbook.model.enumeration.Side;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
@AllArgsConstructor
public class Order implements Comparable<Order> {
    private Side side;
    private double quantity;
    private int price;
    private String currencyPair;
    private int orderCount;

    @Override
    public int compareTo(@NonNull Order order) {
        if (side == Side.SELL) {
            return Integer.compare(this.price, order.price);
        } else {
            return Integer.compare(this.price, order.price) * -1;
        }
    }
}