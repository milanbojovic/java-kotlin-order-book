package com.valr.orderbook.repository;

import com.valr.orderbook.model.Order;
import com.valr.orderbook.model.OrderBook;
import com.valr.orderbook.model.enumeration.Side;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.valr.orderbook.util.TestHelper.BTC_ZAR;
import static com.valr.orderbook.util.TestHelper.createOrdersList;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderBookRepositoryTest {

    private OrderBookRepository orderBookRepository;

    @BeforeEach
    void setUp() {
        orderBookRepository = new OrderBookRepository();
        orderBookRepository.insertData();
    }


    @Test
    void filter_by_currency_pair_returns_correct_size() {
        List<Order> asks = createOrdersList(Side.SELL);
        List<Order> bids = createOrdersList(Side.BUY);
        OrderBook orderBook = OrderBook.builder()
                .asks(asks).bids(bids).build();
        orderBookRepository.setOrderBook(orderBook);

        OrderBook result = orderBookRepository.filterOrderBookBy(BTC_ZAR);
        assertEquals(2, result.getAsks().size());
        assertEquals(2, result.getBids().size());
    }

    @Test
    void create_order_buy_side_no_match_adds_to_order_book() {
        Order order = new Order(Side.BUY, 0.5, 100, BTC_ZAR);
        Order result = orderBookRepository.createOrder(order);

        assertNull(result);
        assertTrue(orderBookRepository.getOrderBook().getBids().contains(order));
    }

    @Test
    void create_order_sell_side_no_match_adds_to_order_book() {
        Order order = new Order(Side.SELL, 0.5, 100, BTC_ZAR);
        Order result = orderBookRepository.createOrder(order);

        assertNull(result);
        assertTrue(orderBookRepository.getOrderBook().getAsks().contains(order));
    }

    @Test
    void create_order_buy_side_full_match_order_removed() {
        Order order = new Order(Side.BUY, 0.5, 100, BTC_ZAR);
        Order matchedOrder = new Order(Side.SELL, 0.5, 100, BTC_ZAR);

        orderBookRepository.createOrder(matchedOrder);
        assertTrue(orderBookRepository.getOrderBook().getAsks().contains(matchedOrder));
        Order result = orderBookRepository.createOrder(order);

        assertEquals(order, result);
        assertFalse(orderBookRepository.getOrderBook().getAsks().contains(matchedOrder));
    }

    @Test
    void create_order_sell_side_full_match_order_removed() {
        Order order = new Order(Side.SELL, 0.5, 100, BTC_ZAR);
        Order matchedOrder = new Order(Side.BUY, 0.5, 100, BTC_ZAR);

        orderBookRepository.createOrder(matchedOrder);
        assertTrue(orderBookRepository.getOrderBook().getBids().contains(matchedOrder));
        Order result = orderBookRepository.createOrder(order);

        assertEquals(order, result);
        assertFalse(orderBookRepository.getOrderBook().getBids().contains(matchedOrder));
    }

    @Test
    void create_order_buy_side_partial_match_updates_quantity() {
        Order order = new Order(Side.BUY, 0.5, 100, BTC_ZAR);
        Order matchedOrder = new Order(Side.SELL, 1.0, 100, BTC_ZAR);

        orderBookRepository.createOrder(matchedOrder);
        Order result = orderBookRepository.createOrder(order);

        assertEquals(order, result);
        assertEquals(0.5, matchedOrder.getQuantity());
        assertTrue(orderBookRepository.getOrderBook().getAsks().contains(matchedOrder));
    }

    @Test
    void create_order_sell_side_partial_match_updates_quantity() {
        Order order = new Order(Side.SELL, 0.5, 100, BTC_ZAR);
        Order matchedOrder = new Order(Side.BUY, 1.0, 100, BTC_ZAR);

        orderBookRepository.createOrder(matchedOrder);
        Order result = orderBookRepository.createOrder(order);

        assertEquals(order, result);
        assertEquals(0.5, matchedOrder.getQuantity());
        assertTrue(orderBookRepository.getOrderBook().getBids().contains(matchedOrder));
    }

    @Test
    void create_order_buy_side_same_price_groups_orders() {
        Order order = new Order(Side.BUY, 0.5, 100, BTC_ZAR);
        Order existingOrder = new Order(Side.BUY, 0.5, 100, BTC_ZAR);

        orderBookRepository.createOrder(existingOrder);
        Order result = orderBookRepository.createOrder(order);

        assertNull(result);
        assertEquals(1.0, existingOrder.getQuantity());
        assertTrue(orderBookRepository.getOrderBook().getBids().contains(existingOrder));
    }

    @Test
    void create_order_sell_side_same_price_groups_orders() {
        Order order = new Order(Side.SELL, 0.5, 100, BTC_ZAR);
        Order existingOrder = new Order(Side.SELL, 0.5, 100, BTC_ZAR);

        orderBookRepository.createOrder(existingOrder);
        Order result = orderBookRepository.createOrder(order);

        assertNull(result);
        assertEquals(1.0, existingOrder.getQuantity());
        assertTrue(orderBookRepository.getOrderBook().getAsks().contains(existingOrder));
    }
}