package com.valr.orderbook.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.valr.orderbook.model.*;
import com.valr.orderbook.model.enumeration.Side;
import com.valr.orderbook.security.JwtUtil;
import com.valr.orderbook.service.OrderBookService;
import com.valr.orderbook.service.TradeHistoryService;
import com.valr.orderbook.service.UserService;
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

import java.util.Optional;

import static com.valr.orderbook.util.TestHelper.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class WebControllerTest {

    public static final String SKIP = "skip";
    public static final String LIMIT = "limit";

    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private OrderBookService orderBookService;
    @Mock
    private TradeHistoryService tradeHistoryService;
    @Mock
    private UserService userService;
    @InjectMocks
    private WebController webController;


    @Autowired
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        webController = new WebController(orderBookService, tradeHistoryService, userService, jwtUtil);
        mockMvc = MockMvcBuilders.standaloneSetup(webController).build();
    }

    @Test
    public void login_with_valid_credentials_returns_token() throws Exception {
        when(jwtUtil.generateToken(anyString())).thenReturn("mockToken");
        when(userService.login(anyString(), anyString())).thenReturn(Optional.of(createTestUser()));

        MvcResult mvcResult = mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserDTO("admin", "admin"))))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponse).contains("Bearer", "mockToken");
    }

    private static User createTestUser() {
        return new User("John", "Doe", "john.doe@valr.com", "john.doe", "pass");
    }

    @Test
    public void login_with_invalid_credentials_returns_unauthorized() throws Exception {
        when(userService.login(anyString(), anyString())).thenReturn(Optional.empty());
        MvcResult mvcResult = mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserDTO("admin1", "admin1"))))
                .andExpect(status().isUnauthorized())
                .andReturn();
        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponse).contains("Invalid username or password.");
    }

    @Test
    public void login_with_null_username_returns_bad_request() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserDTO(null, "admin"))))
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponse).contains("Invalid login request. Please provide a username and password.");
    }

    @Test
    public void login_with_null_pass_returns_bad_request() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserDTO("admin", null))))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponse).contains("Invalid login request. Please provide a username and password.");
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
        verify(orderBookService).getOrderBookBy(BTC_ZAR);
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
    public void create_limit_order_with_valid_data_returns_success() throws Exception {
        LimitOrderDTO limitOrder = new LimitOrderDTO(Side.SELL, 0.5, 100, BTC_ZAR);
        Order executedOrder = createOrder(Side.SELL, 0.5, 100, BTC_ZAR);
        when(orderBookService.createLimitOrder(any())).thenReturn(executedOrder);

        MvcResult mvcResult = mockMvc.perform(post("/api/order/limit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(limitOrder)))
                .andExpect(status().isOk())
                .andReturn();
        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponse).isEqualToIgnoringWhitespace("Limit order created successfully.");
        verify(orderBookService).createLimitOrder(any(LimitOrderDTO.class));
        verify(tradeHistoryService).addTradeOrder(executedOrder);
    }

    @Test
    public void create_limit_order_with_valid_data_but_no_executed_order_returns_success() throws Exception {
        LimitOrderDTO limitOrder = new LimitOrderDTO(Side.SELL, 0.5, 100, BTC_ZAR);
        when(orderBookService.createLimitOrder(any())).thenReturn(null);

        MvcResult mvcResult = mockMvc.perform(post("/api/order/limit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(limitOrder)))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponse).isEqualToIgnoringWhitespace("Limit order created successfully.");
        verify(orderBookService).createLimitOrder(any(LimitOrderDTO.class));
        verify(tradeHistoryService, never()).addTradeOrder(any(Order.class));
    }

    @Test
    public void create_limit_order_with_invalid_data_returns_bad_request() throws Exception {
        LimitOrderDTO limitOrder = new LimitOrderDTO(Side.SELL, 0,0, BTC_ZAR);

        MvcResult mvcResult = mockMvc.perform(post("/api/order/limit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(limitOrder)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponse).isEqualToIgnoringWhitespace("{\"code\":-23,\"message\":\"Invalid limitOrder." +
                " Please provide a 6 character currency pair - valid example: BTCZAR | btczar.\\nQuantity and price " +
                "must be greater than 0.\\nSide must be either 'BUY' or 'SELL'.\"}");
        verify(orderBookService, never()).createLimitOrder(any(LimitOrderDTO.class));
        verify(tradeHistoryService, never()).addTradeOrder(any(Order.class));
    }

    @Test
    public void get_tradehistory_with_valid_parameters_returns_tradehistory() throws Exception {
        TradeHistory tradeHistory = TradeHistory.builder().build();
        when(tradeHistoryService.getTradeHistoryBy(anyString(), anyInt(), anyInt())).thenReturn(tradeHistory);
        MvcResult mvcResult = mockMvc.perform(get("/api/BTCZAR/trades")
                        .param(SKIP, "5")
                        .param(LIMIT, "17")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponse).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(tradeHistory));
        verify(tradeHistoryService).getTradeHistoryBy(eq(BTC_ZAR), eq(5), eq(17));
    }

    @Test
    public void get_tradehistory_withhout_limit_and_skip_parames_calls_with_defaults() throws Exception {
        TradeHistory tradeHistory = TradeHistory.builder().build();
        when(tradeHistoryService.getTradeHistoryBy(anyString(), anyInt(), anyInt())).thenReturn(tradeHistory);
        MvcResult mvcResult = mockMvc.perform(get("/api/BTCZAR/trades")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponse).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(tradeHistory));
        verify(tradeHistoryService).getTradeHistoryBy(eq(BTC_ZAR), eq(0), eq(10));
    }

    @Test
    public void get_tradehistory_with_invalid_currency_pair_returns_error() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/BTC@AR/trades")
                        .param(SKIP, "0")
                        .param(LIMIT, "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponse).isEqualToIgnoringWhitespace("{\"code\":-21,\"message\":\"Invalid currency pair. " +
                "Please provide a 6 character currency pair - valid example: BTCZAR | btczar.\"}");
    }

    @Test
    public void get_tradehistory_with_negative_skip_returns_error() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/BTCZAR/trades")
                        .param(SKIP, "-1")
                        .param(LIMIT, "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponse).isEqualToIgnoringWhitespace("{\"code\":-22,\"message\":\"Invalid skip or limit value. " +
                "Please provide a positive integer value for skip and limit (max limit is 100).\"}");
    }

    @Test
    public void get_tradehistory_with_negative_limit_returns_error() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/BTCZAR/trades")
                        .param(SKIP, "0")
                        .param(LIMIT, "-1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponse).isEqualToIgnoringWhitespace("{\"code\":-22,\"message\":\"Invalid skip or limit value. " +
                "Please provide a positive integer value for skip and limit (max limit is 100).\"}");
    }

    @Test
    public void get_tradehistory_with_limit_exceeding_max_returns_error() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/BTCZAR/trades")
                        .param(SKIP, "0")
                        .param(LIMIT, "101")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponse).isEqualToIgnoringWhitespace("{\"code\":-22,\"message\":\"Invalid skip or limit value. " +
                "Please provide a positive integer value for skip and limit (max limit is 100).\"}");
    }
}