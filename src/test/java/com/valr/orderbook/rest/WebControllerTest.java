package com.valr.orderbook.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.valr.orderbook.model.OrderBook;
import com.valr.orderbook.service.OrderBookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.valr.orderbook.util.TestHelper.createOrderBook;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class WebControllerTest {

    @Mock
    private OrderBookService orderBookService;

    @InjectMocks
    private WebController webController;

    @Autowired
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        webController = new WebController(orderBookService);
        mockMvc = MockMvcBuilders.standaloneSetup(webController).build();
    }

    @Test
    public void getOrderBookWithValidCurrencyPairReturnsOrderBook() throws Exception {
        OrderBook orderBook = createOrderBook();
        when(orderBookService.getOrderBook(anyString())).thenReturn(orderBook);
        MvcResult mvcResult = mockMvc.perform(get("/api/BTCZAR/orderbook")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponse).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(orderBook));
        verify(orderBookService).getOrderBook("BTCZAR");
    }

    @Test
    public void getOrderBookWithInvalidCurrencyPairReturnsError() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/BTCZAR1/orderbook")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponse).isEqualToIgnoringWhitespace("{\"code\":-21,\"message\":\"Invalid currency pair. " +
                "Please provide a 6 character currency pair - valid example: BTCZAR | btczar.\"}");
    }

    @Test
    public void getOrderBookWithSpecialCharacterInCurrencyPairError() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/BTC@AR/orderbook")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponse).isEqualToIgnoringWhitespace("{\"code\":-21,\"message\":\"Invalid currency pair. " +
                "Please provide a 6 character currency pair - valid example: BTCZAR | btczar.\"}");
    }
}