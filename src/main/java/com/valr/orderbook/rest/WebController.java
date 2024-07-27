package com.valr.orderbook.rest;

import com.valr.orderbook.exception.Error;
import com.valr.orderbook.model.LimitOrderDTO;
import com.valr.orderbook.model.Order;
import com.valr.orderbook.model.User;
import com.valr.orderbook.model.UserDTO;
import com.valr.orderbook.security.JwtUtil;
import com.valr.orderbook.service.OrderBookService;
import com.valr.orderbook.service.TradeHistoryService;
import com.valr.orderbook.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * REST controller for handling API requests related to user authentication, order book, and trade history.
 */
@RestController
@RequestMapping("/api/")
public class WebController {
    public static final String CURRENCY_PAIR_PATTERN = "[A-Za-z]{6}";
    public static final String CURRENCY_PAIR_VALIDATION_ERROR = "Invalid currency pair. Please provide a 6 character " +
            "currency pair - valid example: BTCZAR | btczar.";

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private final UserService userService;

    @Autowired
    private final OrderBookService orderBookService;

    @Autowired
    private final TradeHistoryService tradeHistoryService;

    /**
     * Constructor for WebController.
     *
     * @param orderBookService the service for handling order book operations
     * @param tradeHistoryService the service for handling trade history operations
     * @param userService the service for handling user operations
     * @param jwtUtil the utility for handling JWT operations
     */
    @Autowired
    public WebController(OrderBookService orderBookService, TradeHistoryService tradeHistoryService,
                         UserService userService, JwtUtil jwtUtil) {
        this.orderBookService = orderBookService;
        this.tradeHistoryService = tradeHistoryService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Endpoint for user login.
     *
     * @param userDto the user data transfer object containing login credentials
     * @return a ResponseEntity containing a JWT token if login is successful, or an error message if login fails
     */
    @PostMapping("/user/login")
    public ResponseEntity<Object> loginUser(@Valid @RequestBody UserDTO userDto) {
        Optional<User> optUser = userService.login(userDto.getUsername(), userDto.getPassword());
        if (optUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Error(-24,
                    "Invalid login request. Invalid username or password."));
        }
        User existingUser = optUser.get();
        Map<String, String> response = new HashMap<>();
        response.put("Bearer", jwtUtil.generateToken(existingUser.getUsername()));
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint for retrieving the order book for a specific currency pair.
     *
     * @param currencyPair the currency pair to retrieve the order book for
     * @return a ResponseEntity containing the order book or an error message if the currency pair is invalid
     */
    @GetMapping("{currencyPair}/orderbook")
    public ResponseEntity<Object> getOrderBook(@PathVariable String currencyPair) {
        Pattern pattern = Pattern.compile(CURRENCY_PAIR_PATTERN);
        if (!pattern.matcher(currencyPair).matches()) {
            return ResponseEntity.badRequest().body(new Error(-21, CURRENCY_PAIR_VALIDATION_ERROR));
        }
        return ResponseEntity.ok().body(orderBookService.getOrderBookBy(currencyPair));
    }

    /**
     * Endpoint for creating a limit order.
     *
     * @param limitOrder the limit order data transfer object containing order details
     * @return a ResponseEntity containing a success message or an error message if the order is invalid
     */
    @PostMapping("/order/limit")
    public ResponseEntity<Object> createLimitOrder(@Valid @RequestBody LimitOrderDTO limitOrder) {
        if (!limitOrder.getCurrencyPair().matches(CURRENCY_PAIR_PATTERN) || limitOrder.getQuantity() <= 0 ||
                limitOrder.getPrice() <= 0) {
            return ResponseEntity.badRequest().body(new Error(-23, """
                    Invalid limitOrder. Please provide a 6 character currency pair - valid example: BTCZAR | btczar.
                    Quantity and price must be greater than 0.
                    Side must be either 'BUY' or 'SELL'."""));
        }
        Order executedOrder = orderBookService.createLimitOrder(limitOrder);
        if (executedOrder != null) {
            tradeHistoryService.addTradeOrder(executedOrder);
        }
        return ResponseEntity.ok().body("Limit order created successfully.");
    }

    /**
     * Endpoint for retrieving the trade history for a specific currency pair.
     *
     * @param currencyPair the currency pair to retrieve the trade history for
     * @param skip the number of records to skip
     * @param limit the maximum number of records to return
     * @return a ResponseEntity containing the trade history or an error message if the input parameters are invalid
     */
    @SuppressWarnings("ConstantValue")
    @GetMapping("{currencyPair}/trades")
    public ResponseEntity<Object> getTradeHistory(@PathVariable String currencyPair,
                                                  @RequestParam(defaultValue = "0") @Min(0) int skip,
                                                  @RequestParam(defaultValue = "10") @Min(0) int limit) {
        Pattern currencyPairPattern = Pattern.compile(CURRENCY_PAIR_PATTERN);
        if (!currencyPairPattern.matcher(currencyPair).matches()) {
            return ResponseEntity.badRequest().body(new Error(-21, CURRENCY_PAIR_VALIDATION_ERROR));
        } else if (skip < 0 || limit < 0 || limit > 100) {
            return ResponseEntity.badRequest().body(new Error(-22, "Invalid skip or limit value. " +
                    "Please provide a positive integer value for skip and limit (max limit is 100)."));
        }
        return ResponseEntity.ok().body(tradeHistoryService.getTradeHistoryBy(currencyPair, skip, limit));
    }
}