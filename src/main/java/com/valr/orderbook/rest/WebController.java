package com.valr.orderbook.rest;

import com.valr.orderbook.exception.Error;
import com.valr.orderbook.model.LimitOrderDTO;
import com.valr.orderbook.model.Order;
import com.valr.orderbook.service.OrderBookService;
import com.valr.orderbook.service.TradeHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/")
public class WebController {
    private final OrderBookService orderBookService;
    private final TradeHistoryService tradeHistoryService;

    @Autowired
    public WebController(OrderBookService orderBookService, TradeHistoryService tradeHistoryService) {
        this.orderBookService = orderBookService;
        this.tradeHistoryService = tradeHistoryService;
    }

    @GetMapping("{currencyPair}/orderbook")
    public ResponseEntity<Object> getOrderBook(@PathVariable String currencyPair) {
        Pattern pattern = Pattern.compile("[A-Za-z]{6}");
        if (!pattern.matcher(currencyPair).matches()) {
            return ResponseEntity.badRequest().body(new Error(-21, "Invalid currency pair. " +
                    "Please provide a 6 character currency pair - valid example: BTCZAR | btczar."));
        }
        return ResponseEntity.ok().body(orderBookService.getOrderBookBy(currencyPair));
    }

    @GetMapping("{currencyPair}/tradehistory")
    public ResponseEntity<Object> getTradeHistory(@PathVariable String currencyPair,
                                                  @RequestParam(defaultValue = "0") @Min(0) int skip,
                                                  @RequestParam(defaultValue = "10") @Min(0) int limit) {
        Pattern currencyPairPattern = Pattern.compile("[A-Za-z]{6}");
        if (!currencyPairPattern.matcher(currencyPair).matches()) {
            return ResponseEntity.badRequest().body(new Error(-21, "Invalid currency pair. " +
                    "Please provide a 6 character currency pair - valid example: BTCZAR | btczar."));
        } else if (skip < 0 || limit < 0 || limit > 100) {
            return ResponseEntity.badRequest().body(new Error(-22, "Invalid skip or limit value. " +
                    "Please provide a positive integer value for skip and limit (max limit is 100)."));
        }
        return ResponseEntity.ok().body(tradeHistoryService.getTradeHistoryBy(currencyPair, skip, limit));
    }
}