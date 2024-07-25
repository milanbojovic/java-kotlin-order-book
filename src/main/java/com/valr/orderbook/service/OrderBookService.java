package com.valr.orderbook.service;

import com.valr.orderbook.model.LimitOrderDTO;
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

    public OrderBook getOrderBookBy(String currencyPair) {
        return orderBookRepository.filterOrderBookBy(currencyPair.toUpperCase());
    }

    public void updateOrderBook(OrderBook orderBook) {
        orderBookRepository.setOrderBook(orderBook);
    }

    public Order createLimitOrder(LimitOrderDTO limitOrderDTO) {
        return orderBookRepository.createOrder(new Order(limitOrderDTO));
    }
}