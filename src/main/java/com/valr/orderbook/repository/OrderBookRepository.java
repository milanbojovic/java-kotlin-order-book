package com.valr.orderbook.repository;

import com.valr.orderbook.model.Order;
import com.valr.orderbook.model.OrderBook;
import com.valr.orderbook.model.enumeration.Side;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

@Component
public class OrderBookRepository {
    OrderBook orderBook;

    public OrderBookRepository() {
        orderBook = OrderBook.builder().build();
    }

    public OrderBook filterByCurrencyPair(String currencyPair) {
        return OrderBook.builder()
                .asks(Optional.ofNullable(orderBook.getAsks()).orElse(Collections.emptyList())
                        .stream()
                        .filter(order -> order.getCurrencyPair().equals(currencyPair))
                        .toList())
                .bids(Optional.ofNullable(orderBook.getBids()).orElse(Collections.emptyList()).stream()
                        .filter(order -> order.getCurrencyPair().equals(currencyPair))
                        .toList())
                .lastChange(orderBook.getLastChange())
                .sequenceNumber(orderBook.getSequenceNumber())
                .build();
    }

    public void updateOrderBook(OrderBook orderBook) {
        this.orderBook = orderBook;
    }

    /**
     * Adding initial data to the repository on startup
     * This is just for easier presentation purposes for live sytstem I would add initialization in unit tests
     */
    @PostConstruct
    public void insertData() {
        OrderBook orderBook = createExampleOrderBook();
        orderBook.setLastChange(Instant.now().toString());
        orderBook.setSequenceNumber(orderBook.getSequenceNumber() + 1);
        updateOrderBook(orderBook);
    }

    private static OrderBook createExampleOrderBook() {
        OrderBook orderBook = OrderBook.builder()
                .sequenceNumber(1)
                .build();
        orderBook.setAsks(Arrays.asList(
                new Order(Side.SELL, 0.02352094, 1205649, "BTCZAR", 1),
                new Order(Side.SELL, 0.552, 1205653, "BTCZAR", 1),
                new Order(Side.SELL, 0.0008979, 1205748, "ETHUSD", 1),
                new Order(Side.SELL, 0.00100004, 1203000, "BTCZAR", 1),
                new Order(Side.SELL, 0.02350766, 1202530, "BTCEUR", 1),
                new Order(Side.SELL, 0.90038334, 1186331, "BTCEUR", 1),
                new Order(Side.SELL, 0.001, 1207000, "BTCZAR", 1)
        ));
        orderBook.setBids(Arrays.asList(
                new Order(Side.BUY, 0.016, 1204994, "BTCZAR", 4),
                new Order(Side.BUY, 0.002036, 1204993, "BTCZAR", 1),
                new Order(Side.BUY, 0.18443981, 1204991, "ETHUSD", 1),
                new Order(Side.BUY, 0.00008142, 1204811, "BTCEUR", 1),
                new Order(Side.BUY, 0.02354031, 1204657, "BTCEUR", 1),
                new Order(Side.BUY, 0.05, 1164656, "BTCZAR", 1),
                new Order(Side.BUY, 0.11498758, 1204532, "BTCZAR", 1)
        ));
        return orderBook;
    }
}
