package com.valr.orderbook.rest;

import com.valr.orderbook.service.OrderBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.valr.orderbook.model.Error;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/")
public class WebController {
    private final OrderBookService orderBookService;
    @Autowired
    public WebController(OrderBookService orderBookService) {
        this.orderBookService = orderBookService;
    }

    @GetMapping("{currencyPair}/orderbook")
    public ResponseEntity<Object> getOrderBook(@PathVariable String currencyPair) {
        Pattern pattern = Pattern.compile("[A-Za-z]{6}");
        if (!pattern.matcher(currencyPair).matches()) {
            return ResponseEntity.badRequest().body(new Error(-21, "Invalid currency pair. " +
                    "Please provide a 6 character currency pair - valid example: BTCZAR | btczar."));
        }
        return ResponseEntity.ok().body(orderBookService.getOrderBook(currencyPair));
    }
}