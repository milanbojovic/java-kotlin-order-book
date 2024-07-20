package com.valr.orderbook.repository;

import com.valr.orderbook.model.Order;
import com.valr.orderbook.model.OrderBook;
import com.valr.orderbook.model.enumeration.Side;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.valr.orderbook.util.TestHelper.createOrdersList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class OrderBookRepositoryTest {

    @InjectMocks
    private OrderBookRepository orderBookRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void filterByCurrencyPairReturnsCorrectSize() {
        List<Order> asks = createOrdersList(Side.SELL);
        List<Order> bids = createOrdersList(Side.BUY);
        OrderBook orderBook = OrderBook.builder()
                .asks(asks).bids(bids).build();
        orderBookRepository.updateOrderBook(orderBook);

        OrderBook result = orderBookRepository.filterByCurrencyPair("BTCZAR");
        assertEquals(2, result.getAsks().size());
        assertEquals(2, result.getBids().size());
    }
}