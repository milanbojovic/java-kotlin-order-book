package com.valr.orderbook.repository;

import com.valr.orderbook.model.Order;
import com.valr.orderbook.model.OrderBook;
import com.valr.orderbook.model.enumeration.Side;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.valr.orderbook.model.util.CurrencyPairConstants.*;

/**
 * Repository class for managing the order book (in memory - easily switchable to database if needed).
 */
@Component
@Data
public class OrderBookRepository {
    OrderBook orderBook;

    /**
     * Constructor for OrderBookRepository.
     * Initializes the order book.
     */
    public OrderBookRepository() {
        orderBook = OrderBook.builder().build();
    }

    /**
     * Filters the order book by the specified currency pair.
     *
     * @param currencyPair the currency pair to filter by
     * @return a filtered OrderBook object
     */
    public OrderBook filterOrderBookBy(String currencyPair) {
        return OrderBook.builder()
                .asks(filterOrderBookList(orderBook.getAsks(), currencyPair))
                .bids(filterOrderBookList(orderBook.getBids(), currencyPair))
                .lastChange(orderBook.getLastChange())
                .build();
    }

    /**
     * Filters a list of orders by the specified currency pair.
     *
     * @param orderBook the list of orders to filter
     * @param currencyPair the currency pair to filter by
     * @return a filtered list of orders
     */
    private List<Order> filterOrderBookList(List<Order> orderBook, String currencyPair) {
        return orderBook.stream()
                .filter(order -> order.getCurrencyPair().equals(currencyPair))
                .collect(Collectors.toList());
    }

    /**
     * Creates a new order and tries to match it with existing orders if possible.
     * Also does the necessary housekeeping activities like updating the quantity or removing the matched order
     * from the list.
     *
     * @param order the order to create
     * @return the matched order if a match is found, otherwise null
     */
    public Order createOrder(Order order) {
        Optional<Order> matchOrder = matchOppositeOrderType(order);
        if (matchOrder.isPresent()) {
            return matchedOrderExecution(order, matchOrder.get());
        }
        unmatchedOrderExecution(order);
        return null;
    }

    /**
     * Executes logic for matched order. If the quantities are equal, the matched order is removed.
     * Otherwise, the matched order's quantity is updated and the order book is sorted to reflect possible
     * order update due to change in quantity.
     *
     * @param order the order to execute
     * @param matchedOrder the matched order
     * @return the executed order
     */
    private Order matchedOrderExecution(Order order, Order matchedOrder) {
        double quantityDiff = subQuantities(order, matchedOrder);
        if (quantityDiff == 0) {
            removeOrder(matchedOrder);
        } else {
            matchedOrder.setQuantity(quantityDiff);
        }
        return order;
    }

    /**
     * Executes logic for unmatched order in opposite Side.
     * It tries to match the order with the same price. If found, the quantities are added.
        * Otherwise, the order is added to the order book and the order book is sorted.
     *
     * @param order the order to execute
     */
    private void unmatchedOrderExecution(Order order) {
        matchSamePriceOrder(order).ifPresentOrElse(
                matchedOrder -> matchedOrder.setQuantity(addQuantities(order, matchedOrder)),
                () -> addToOrderBook(order)
        );
    }

    /**
     * Subtracts the quantities of two orders.
     *
     * @param order the order to subtract from
     * @param matchedOrder the order to subtract
     * @return the difference in quantities
     */
    private static double subQuantities(Order order, Order matchedOrder) {
        return matchedOrder.getQuantity() - order.getQuantity();
    }

    /**
     * Adds the quantities of two orders.
     *
     * @param order the order to add to
     * @param matchedOrder the order to add
     * @return the sum of quantities
     */
    private static double addQuantities(Order order, Order matchedOrder) {
        return matchedOrder.getQuantity() + order.getQuantity();
    }

    /**
     * Sorts the order book by inspecting the Side of specified order.
     *
     * @param order the order to sort by
     */
    private void sortOrderBookBy(Order order) {
        if (order.getSide() == Side.BUY) {
            Collections.sort(orderBook.getBids());
        } else {
            Collections.sort(orderBook.getAsks());
        }
    }

    /**
     * Adds a new order to the order book list.
     *
     * @param newOrder the order to add
     */
    private void addToOrderBook(Order newOrder) {
        if (newOrder.getSide() == Side.BUY) {
            orderBook.getBids().add(newOrder);
        } else {
            orderBook.getAsks().add(newOrder);
        }
        sortOrderBookBy(newOrder);
    }

    /**
     * Removes an order from the corresponding collection Asks/Bids by inspecting the Side of the Order.
     *
     * @param order the order to remove
     */
    private void removeOrder(Order order) {
        if (order.getSide() == Side.BUY) {
            orderBook.getBids().remove(order);
        }
        orderBook.getAsks().remove(order);
    }

    /**
     * Matches an order with the opposite order type.
     *
     * @param order the order to match
     * @return an optional containing the matched order if found, otherwise empty
     */
    private Optional<Order> matchOppositeOrderType(Order order) {
        if (order.getSide() == Side.BUY) {
            return matchBuyOrdersByPriceAndQuantity(order);
        }
        return matchSellOrdersByPriceAndQuantity(order);
    }

    /**
     * Matches a buy order with the asks.
     *
     * @param order the buy order to match
     * @return an optional containing the matched ask if found, otherwise empty
     */
    private Optional<Order> matchBuyOrdersByPriceAndQuantity(Order order) {
        return orderBook.getAsks().stream()
                .filter(ask -> ask.getPrice() <= order.getPrice())
                .filter(ask -> ask.getQuantity() >= order.getQuantity())
                .findFirst();
    }

    /**
     * Matches a sell order with the bids.
     *
     * @param order the sell order to match
     * @return an optional containing the matched bid if found, otherwise empty
     */
    private Optional<Order> matchSellOrdersByPriceAndQuantity(Order order) {
        return orderBook.getBids().stream()
                .filter(bid -> bid.getPrice() >= order.getPrice())
                .filter(bid -> bid.getQuantity() >= order.getQuantity())
                .findFirst();
    }

    /**
     * Matches an order with the same price.
     *
     * @param order the order to match
     * @return an optional containing the matched order if found, otherwise empty
     */
    private Optional<Order> matchSamePriceOrder(Order order) {
        List<Order> orders = order.getSide() == Side.BUY ? orderBook.getBids() : orderBook.getAsks();
        return orders.stream()
                .filter(filterOrder -> filterOrder.getPrice() == order.getPrice())
                .findFirst();
    }

    /**
     * Adds initial data to the repository on startup.
     * This is just for easier presentation purposes; for a live system, initialization would be added in unit tests.
     */
    @PostConstruct
    public void insertData() {
        OrderBook orderBook = createExampleOrderBook();
        orderBook.setLastChange(Instant.now().toString());
        setOrderBook(orderBook);
    }

    /**
     * Creates an example order book with initial data.
     *
     * @return an OrderBook object with initial data
     */
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