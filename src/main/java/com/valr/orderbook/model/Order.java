package com.valr.orderbook.model;

import com.valr.orderbook.model.enumeration.Side;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
public class Order implements Comparable<Order> {
    private Side side;
    private double quantity;
    private int price;
    private String currencyPair;

    public Order(Order order) {
        this.side = order.getSide();
        this.quantity = order.getQuantity();
        this.price = order.getPrice();
        this.currencyPair = order.getCurrencyPair();
    }

    public Order(LimitOrderDTO orderDTO) {
        this.side = orderDTO.getSide();
        this.quantity = orderDTO.getQuantity();
        this.price = orderDTO.getPrice();
        this.currencyPair = orderDTO.getCurrencyPair();
    }



    @Override
    public int compareTo(@NonNull Order order) {
        if (side == Side.SELL) {
            return Integer.compare(this.price, order.price);
        } else {
            return Integer.compare(this.price, order.price) * -1;
        }
    }
}