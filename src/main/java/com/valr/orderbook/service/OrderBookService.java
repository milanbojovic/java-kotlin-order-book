package com.valr.orderbook.service;

import com.valr.orderbook.model.LimitOrderDTO;
import com.valr.orderbook.model.Order;
import com.valr.orderbook.model.OrderBook;
import com.valr.orderbook.repository.OrderBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class for managing order book operations.
 */
@Service
public class OrderBookService {

    private final OrderBookRepository orderBookRepository;

    /**
     * Constructor for OrderBookService.
     *
     * @param orderBookRepository the repository for managing order book data
     */
    @Autowired
    public OrderBookService(OrderBookRepository orderBookRepository) {
        this.orderBookRepository = orderBookRepository;
    }

    /**
     * Retrieves the order book for a given currency pair.
     *
     * @param currencyPair the currency pair to filter the order book by
     * @return the filtered order book
     */
    public OrderBook getOrderBookBy(String currencyPair) {
        return orderBookRepository.filterOrderBookBy(currencyPair.toUpperCase());
    }

    /**
     * Updates the order book with the given order book data.
     * Used to update the order book for inserting data (not for production).
     * @param orderBook the order book data to update
     */
    public void updateOrderBook(OrderBook orderBook) {
        orderBookRepository.setOrderBook(orderBook);
    }

    /**
     * Creates a limit order based on the given limit order DTO.
     *
     * @param limitOrderDTO the limit order data transfer object
     * @return the created order
     */
    public Order createLimitOrder(LimitOrderDTO limitOrderDTO) {
        return orderBookRepository.createOrder(new Order(limitOrderDTO));
    }
}