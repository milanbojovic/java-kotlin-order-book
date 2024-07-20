package com.valr.orderbook.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderBook {
    private List<Order> asks;
    private List<Order> bids;
    private String lastChange;
    private long sequenceNumber;
}