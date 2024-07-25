package com.valr.orderbook.model;

import com.valr.orderbook.model.enumeration.Side;
import lombok.AllArgsConstructor;
import lombok.Data;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
public class LimitOrderDTO {
    @NotBlank(message = "Side is required.")
    @Pattern(regexp = "^(BUY|SELL)$", message = "Side must be either 'BUY' or 'SELL'.")
    private Side side;

    @Min(value = 1, message = "Quantity must be greater than 0.")
    private double quantity;

    @Min(value = 1, message = "Price must be greater than 0.")
    private int price;

    @NotBlank(message = "Currency pair is required.")
    private String currencyPair;
}