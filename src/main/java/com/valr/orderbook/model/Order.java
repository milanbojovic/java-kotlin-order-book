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
        this(order.side, order.quantity, order.price, order.currencyPair);
    }

    public Order(LimitOrderDTO orderDTO) {
        this(orderDTO.getSide(), orderDTO.getQuantity(), orderDTO.getPrice(), orderDTO.getCurrencyPair());
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