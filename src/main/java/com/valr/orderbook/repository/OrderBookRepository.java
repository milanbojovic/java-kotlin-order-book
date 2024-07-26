package com.valr.orderbook.repository;

import com.valr.orderbook.model.Order;
import com.valr.orderbook.model.OrderBook;
import com.valr.orderbook.model.enumeration.Side;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;

import static com.valr.orderbook.model.util.CurrencyPairConstants.*;

@Component
@Data
public class OrderBookRepository {
    OrderBook orderBook;

    public OrderBookRepository() {
        orderBook = OrderBook.builder().build();
    }


    public OrderBook filterOrderBookBy(String currencyPair) {
        return OrderBook.builder()
                .asks(filterOrderBookList(orderBook.getAsks(), currencyPair))
                .bids(filterOrderBookList(orderBook.getBids(), currencyPair))
                .lastChange(orderBook.getLastChange())
                .build();
    }

    private List<Order> filterOrderBookList(List<Order> orderBook, String currencyPair) {
        return Optional.ofNullable(orderBook).orElse(Collections.emptyList())
                .stream()
                .filter(order -> order.getCurrencyPair().equals(currencyPair))
                .toList();
    }

    public Order createOrder(Order order) {
        Optional<Order> matchOrder = matchOppositeOrderType(order);
        if (matchOrder.isPresent()) {
            return matchedOrderExecution(order, matchOrder.get());
        }
        unmatchedOrderExecution(order);
        return null;
    }

    private Order matchedOrderExecution(Order order, Order matchedOrder) {
        double quantityDiff = subQuantities(order, matchedOrder);
        if (quantityDiff == 0) {
            removeOrder(matchedOrder);
        } else {
            matchedOrder.setQuantity(quantityDiff);
            sortOrderBookBy(matchedOrder);
        }
        return order;
    }

    private void unmatchedOrderExecution(Order order) {
        matchSamePriceOrder(order).ifPresentOrElse(
                (matchedOrder) -> {
                    matchedOrder.setQuantity(addQuantities(order, matchedOrder));
                    sortOrderBookBy(order);
                },
                () -> addToOrderBook(order)
        );
        sortOrderBookBy(order);
    }

    private static double subQuantities(Order order, Order matchedOrder) {
        return matchedOrder.getQuantity() - order.getQuantity();
    }

    private static double addQuantities(Order order, Order matchedOrder) {
        return matchedOrder.getQuantity() + order.getQuantity();
    }

    private void sortOrderBookBy(Order order) {
        if (order.getSide() == Side.BUY) {
            Collections.sort(orderBook.getBids());
        } else {
            Collections.sort(orderBook.getAsks());
        }
    }

    private void addToOrderBook(Order newOrder) {
        if (newOrder.getSide() == Side.BUY) {
            orderBook.getBids().add(newOrder);
        } else {
            orderBook.getAsks().add(newOrder);
        }
        sortOrderBookBy(newOrder);
    }

    private void removeOrder(Order order) {
        if (order.getSide() == Side.BUY) {
            orderBook.getBids().remove(order);
        }
        orderBook.getAsks().remove(order);
    }

    private Optional<Order> matchOppositeOrderType(Order order) {
        if (order.getSide() == Side.BUY) {
            return matchWithAsks(order);
        }
        return matchWithBids(order);
    }

    private Optional<Order> matchWithAsks(Order order) {
        return orderBook.getAsks().stream()
                .filter(ask -> ask.getPrice() <= order.getPrice())
                .filter(ask -> ask.getQuantity() >= order.getQuantity())
                .findFirst();
    }

    private Optional<Order> matchWithBids(Order order) {
        return orderBook.getBids().stream()
                .filter(bid -> bid.getPrice() >= order.getPrice())
                .filter(bid -> bid.getQuantity() >= order.getQuantity())
                .findFirst();
    }

    private Optional<Order> matchSamePriceOrder(Order order) {
        List<Order> orders = order.getSide() == Side.BUY ? orderBook.getBids() : orderBook.getAsks();
        return orders.stream()
                    .filter(filterOrder -> filterOrder.getPrice() == order.getPrice())
                    .findFirst();
    }

    /**
     * Adding initial data to the repository on startup
     * This is just for easier presentation purposes for live sytstem I would add initialization in unit tests
     */
    @PostConstruct
    public void insertData() {
        OrderBook orderBook = createExampleOrderBook();
        orderBook.setLastChange(Instant.now().toString());
        setOrderBook(orderBook);
    }

    private static OrderBook createExampleOrderBook() {
        OrderBook orderBook = OrderBook.builder()
                .build();
        orderBook.setAsks(new ArrayList<>(Arrays.asList(
                new Order(Side.SELL, 0.90038334, 1186331, BTC_EUR),
                new Order(Side.SELL, 0.02350766, 1202530, BTC_EUR),
                new Order(Side.SELL, 0.00100004, 1203000, BTC_ZAR),
                new Order(Side.SELL, 0.02352094, 1205649, BTC_ZAR),
                new Order(Side.SELL, 0.552, 1205653, BTC_ZAR),
                new Order(Side.SELL, 0.0008979, 1205748, ETH_USD),
                new Order(Side.SELL, 0.001, 1207000, BTC_ZAR)
        )));
        orderBook.setBids(new ArrayList<>(Arrays.asList(
                new Order(Side.BUY, 0.016, 1204994, BTC_ZAR),
                new Order(Side.BUY, 0.002036, 1204993, BTC_ZAR),
                new Order(Side.BUY, 0.18443981, 1204991, ETH_USD),
                new Order(Side.BUY, 0.00008142, 1204811, BTC_EUR),
                new Order(Side.BUY, 0.02354031, 1204657, BTC_EUR),
                new Order(Side.BUY, 0.11498758, 1204532, BTC_ZAR),
                new Order(Side.BUY, 0.05, 1164656, BTC_ZAR)
        )));
        return orderBook;
    }
}
