package com.valr.orderbook.model;

import com.valr.orderbook.model.enumeration.Side;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LimitOrderDTO {
    private Side side;
    private double quantity;
    private int price;
    private String currencyPair;
}