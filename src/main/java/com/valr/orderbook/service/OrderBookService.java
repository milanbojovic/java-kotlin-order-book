package com.valr.orderbook.service;

import com.valr.orderbook.model.Order;
import com.valr.orderbook.model.OrderBook;
import com.valr.orderbook.repository.OrderBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderBookService {

    private final OrderBookRepository orderBookRepository;

    @Autowired
    public OrderBookService(OrderBookRepository orderBookRepository) {
        this.orderBookRepository = orderBookRepository;
    }

    public OrderBook getOrderBook(String currencyPair) {
        return orderBookRepository.filterByCurrencyPair(currencyPair.toUpperCase());
    }

    public void updateOrderBook(OrderBook orderBook) {
        sortLists(orderBook);
        orderBookRepository.updateOrderBook(orderBook);
    }

    private static void sortLists(OrderBook orderBook) {
        orderBook.setBids(orderBook.getBids().stream().sorted(Order::compareTo).toList());
        orderBook.setAsks(orderBook.getAsks().stream().sorted(Order::compareTo).toList());
    }
}