package com.valr.orderbook.service;

import com.valr.orderbook.model.Order;
import com.valr.orderbook.model.OrderBook;
import com.valr.orderbook.model.enumeration.Side;
import com.valr.orderbook.repository.OrderBookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.valr.orderbook.model.LimitOrderDTO;
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
    void get_order_book_returns_correct_data() {
        String currencyPair = "BTCZAR";
        OrderBook expectedOrderBook = OrderBook.builder().build();
        when(orderBookRepository.filterOrderBookBy(currencyPair)).thenReturn(expectedOrderBook);
        OrderBook result = orderBookService.getOrderBookBy(currencyPair);

        assertEquals(expectedOrderBook, result);
        verify(orderBookRepository, times(1)).filterOrderBookBy(currencyPair);
    }

    @Test
    void create_limit_order_creates_order_successfully() {
        LimitOrderDTO limitOrderDTO = new LimitOrderDTO(Side.BUY, 10, 100, "BTCZAR");
        Order expectedOrder = new Order(limitOrderDTO);
        when(orderBookRepository.createOrder(any(Order.class))).thenReturn(expectedOrder);

        Order result = orderBookService.createLimitOrder(limitOrderDTO);

        assertEquals(expectedOrder, result);
        verify(orderBookRepository, times(1)).createOrder(any(Order.class));
    }

    @Test
    void update_order_book_does_not_sort_data() {
        //TODO remove
        OrderBook orderBook = createOrderBook();
        orderBookService.updateOrderBook(orderBook);

        verify(orderBookRepository, times(1)).setOrderBook(orderBook);
        assertEquals(1, orderBook.getBids().get(0).getPrice());
        assertEquals(2, orderBook.getBids().get(1).getPrice());
        assertEquals(3, orderBook.getBids().get(2).getPrice());
        assertEquals(1, orderBook.getAsks().get(0).getPrice());
        assertEquals(2, orderBook.getAsks().get(1).getPrice());
        assertEquals(3, orderBook.getAsks().get(2).getPrice());
    }
}