package com.valr.orderbook.repository;

import com.valr.orderbook.model.Trade;
import com.valr.orderbook.model.TradeHistory;
import com.valr.orderbook.model.enumeration.Side;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TradeHistoryRepositoryTest {

    private TradeHistoryRepository tradeHistoryRepository;

    @BeforeEach
    void setUp() {
        tradeHistoryRepository = new TradeHistoryRepository();
        tradeHistoryRepository.insertData();
        addAdditionalTestData();
    }

    private void addAdditionalTestData() {
        List<Trade> additionalTrades = List.of(
                new Trade(tradeHistoryRepository.getNextAvailableId(), 1234567, 0.001, "BTCZAR", "2024-12-01T10:00:00.000Z", Side.BUY, 50000.0),
                new Trade(tradeHistoryRepository.getNextAvailableId(), 1234568, 0.002, "BTCZAR", "2024-12-01T10:05:00.000Z", Side.SELL, 51000.0),
                new Trade(tradeHistoryRepository.getNextAvailableId(), 1234569, 0.003, "BTCUSD", "2024-12-01T10:10:00.000Z", Side.BUY, 60000.0),
                new Trade(tradeHistoryRepository.getNextAvailableId(), 1234570, 0.004, "BTCUSD", "2024-12-01T10:15:00.000Z", Side.SELL, 61000.0)
        );
        additionalTrades.forEach(tradeHistoryRepository::addTrade);
    }

    @Test
    void filter_trade_history_by_currency_pair_returns_correct_trades() {
        TradeHistory result = tradeHistoryRepository.filterTradeHistoryBy("BTCZAR", 0, 10);
        assertEquals(8, result.getTrades().size());
    }

    @Test
    void filter_trade_history_by_currency_pair_with_skip_and_limit() {
        TradeHistory result = tradeHistoryRepository.filterTradeHistoryBy("BTCZAR", 2, 3);
        assertEquals(3, result.getTrades().size());
        assertEquals(7, result.getTrades().get(0).getId());
    }

    @Test
    void filter_trade_history_by_currency_pair_with_no_trades() {
        TradeHistory result = tradeHistoryRepository.filterTradeHistoryBy("LTCUSD", 0, 10);
        assertTrue(result.getTrades().isEmpty());
    }

    @Test
    void filter_trade_history_by_currency_pair_with_skip_exceeding_trades() {
        TradeHistory result = tradeHistoryRepository.filterTradeHistoryBy("BTCZAR", 10, 5);
        assertTrue(result.getTrades().isEmpty());
    }

    @Test
    void filter_trade_history_by_currency_pair_with_limit_exceeding_trades() {
        TradeHistory result = tradeHistoryRepository.filterTradeHistoryBy("BTCZAR", 0, 20);
        assertEquals(8, result.getTrades().size());
    }
}