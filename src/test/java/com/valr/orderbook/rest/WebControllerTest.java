package com.valr.orderbook.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.valr.orderbook.model.OrderBook;
import com.valr.orderbook.model.TradeHistory;
import com.valr.orderbook.service.OrderBookService;
import com.valr.orderbook.service.TradeHistoryService;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class WebControllerTest {

    @Mock
    private OrderBookService orderBookService;
    @Mock
    private TradeHistoryService tradeHistoryService;

    @InjectMocks
    private WebController webController;

    @Autowired
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        webController = new WebController(orderBookService, tradeHistoryService);
        mockMvc = MockMvcBuilders.standaloneSetup(webController).build();
    }

    @Test
    public void get_orderbook_with_valid_currency_pair_returns_orderbook() throws Exception {
        OrderBook orderBook = createOrderBook();
        when(orderBookService.getOrderBookBy(anyString())).thenReturn(orderBook);
        MvcResult mvcResult = mockMvc.perform(get("/api/BTCZAR/orderbook")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponse).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(orderBook));
        verify(orderBookService).getOrderBookBy("BTCZAR");
    }

    @Test
    public void get_orderbook_with_invalid_currency_pair_returns_error() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/BTCZAR1/orderbook")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponse).isEqualToIgnoringWhitespace("{\"code\":-21,\"message\":\"Invalid currency pair. " +
                "Please provide a 6 character currency pair - valid example: BTCZAR | btczar.\"}");
    }

    @Test
    public void get_orderbook_with_special_character_in_currency_pair_error() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/BTC@AR/orderbook")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponse).isEqualToIgnoringWhitespace("{\"code\":-21,\"message\":\"Invalid currency pair. " +
                "Please provide a 6 character currency pair - valid example: BTCZAR | btczar.\"}");
    }

    @Test
    public void get_tradehistory_with_valid_parameters_returns_tradehistory() throws Exception {
        TradeHistory tradeHistory = TradeHistory.builder().build();
        when(tradeHistoryService.getTradeHistoryBy(anyString(), anyInt(), anyInt())).thenReturn(tradeHistory);
        MvcResult mvcResult = mockMvc.perform(get("/api/BTCZAR/tradehistory")
                        .param("skip", "5")
                        .param("limit", "17")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponse).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(tradeHistory));
        verify(tradeHistoryService).getTradeHistoryBy(eq("BTCZAR"), eq(5), eq(17));
    }

    @Test
    public void get_tradehistory_withhout_limit_and_skip_parames_calls_with_defaults() throws Exception {
        TradeHistory tradeHistory = TradeHistory.builder().build();
        when(tradeHistoryService.getTradeHistoryBy(anyString(), anyInt(), anyInt())).thenReturn(tradeHistory);
        MvcResult mvcResult = mockMvc.perform(get("/api/BTCZAR/tradehistory")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponse).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(tradeHistory));
        verify(tradeHistoryService).getTradeHistoryBy(eq("BTCZAR"), eq(0), eq(10));
    }

    @Test
    public void get_tradehistory_with_invalid_currency_pair_returns_error() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/BTC@AR/tradehistory")
                        .param("skip", "0")
                        .param("limit", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponse).isEqualToIgnoringWhitespace("{\"code\":-21,\"message\":\"Invalid currency pair. " +
                "Please provide a 6 character currency pair - valid example: BTCZAR | btczar.\"}");
    }

    @Test
    public void get_tradehistory_with_negative_skip_returns_error() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/BTCZAR/tradehistory")
                        .param("skip", "-1")
                        .param("limit", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponse).isEqualToIgnoringWhitespace("{\"code\":-22,\"message\":\"Invalid skip or limit value. " +
                "Please provide a positive integer value for skip and limit (max limit is 100).\"}");
    }

    @Test
    public void get_tradehistory_with_negative_limit_returns_error() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/BTCZAR/tradehistory")
                        .param("skip", "0")
                        .param("limit", "-1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponse).isEqualToIgnoringWhitespace("{\"code\":-22,\"message\":\"Invalid skip or limit value. " +
                "Please provide a positive integer value for skip and limit (max limit is 100).\"}");
    }

    @Test
    public void get_tradehistory_with_limit_exceeding_max_returns_error() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/BTCZAR/tradehistory")
                        .param("skip", "0")
                        .param("limit", "101")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponse).isEqualToIgnoringWhitespace("{\"code\":-22,\"message\":\"Invalid skip or limit value. " +
                "Please provide a positive integer value for skip and limit (max limit is 100).\"}");
    }
}