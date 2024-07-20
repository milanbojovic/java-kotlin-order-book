package com.valr.orderbook.service;

import com.valr.orderbook.model.OrderBook;
import com.valr.orderbook.repository.OrderBookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.valr.orderbook.util.TestHelper.createOrderBook;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class OrderBookServiceTest {

    @Mock
    private OrderBookRepository orderBookRepository;

    @InjectMocks
    private OrderBookService orderBookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getOrderBookReturnsCorrectData() {
        String currencyPair = "BTCZAR";
        OrderBook expectedOrderBook = OrderBook.builder().build();
        when(orderBookRepository.filterByCurrencyPair(currencyPair)).thenReturn(expectedOrderBook);
        OrderBook result = orderBookService.getOrderBook(currencyPair);

        assertEquals(expectedOrderBook, result);
        verify(orderBookRepository, times(1)).filterByCurrencyPair(currencyPair);
    }

    @Test
    void updateOrderBookSortsDataCorrectly() {
        OrderBook orderBook = createOrderBook();
        orderBookService.updateOrderBook(orderBook);

        // Verify that the sortLists method was called
        verify(orderBookRepository, times(1)).updateOrderBook(orderBook);
        // Verify that the bids and asks lists are sorted correctly
        assertEquals(3, orderBook.getBids().get(0).getPrice());
        assertEquals(2, orderBook.getBids().get(1).getPrice());
        assertEquals(1, orderBook.getBids().get(2).getPrice());
        assertEquals(1, orderBook.getAsks().get(0).getPrice());
        assertEquals(2, orderBook.getAsks().get(1).getPrice());
        assertEquals(3, orderBook.getAsks().get(2).getPrice());
    }
}