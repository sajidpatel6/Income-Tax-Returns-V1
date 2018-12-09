package org.ssp.itr;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class LIFOMatcher {
    private static void findLIFOTrades(final Transaction sellTrans, final List<Transaction> buyTransList,
            final List<Trade> tradeList) {
        for (final Transaction buyTrans : buyTransList) {
            if (buyTrans.getBalQuantity().compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }
            if (buyTrans.getBalQuantity().compareTo(sellTrans.getBalQuantity()) > 0) {
                final Trade trade = new Trade(buyTrans, sellTrans, sellTrans.getBalQuantity());
                tradeList.add(trade);
                break;
            } else if (buyTrans.getBalQuantity().compareTo(sellTrans.getBalQuantity()) == 0) {
                final Trade trade = new Trade(buyTrans, sellTrans, sellTrans.getBalQuantity());
                tradeList.add(trade);
                break;
            }
            if (buyTrans.getBalQuantity().compareTo(sellTrans.getBalQuantity()) < 0) {
                final Trade trade = new Trade(buyTrans, sellTrans, buyTrans.getBalQuantity());
                tradeList.add(trade);
            }
        }
    }

    private static void printTrades(final List<Trade> tradeList) {
        for (final Trade trade : tradeList) {
            trade.print();
        }
    }

    public static void process(final Map<String, List<Transaction>> stockTransMap) {
        for (final Entry<String, List<Transaction>> entry : stockTransMap.entrySet()) {
            final List<Transaction> transList = entry.getValue();
            final List<Transaction> SellTrans = new ArrayList<Transaction>();
            final List<Transaction> BuyTrans = new ArrayList<Transaction>();
            for (final Transaction transaction : transList) {
                if ("Sell".equalsIgnoreCase(transaction.getAction())) {
                    SellTrans.add(transaction);
                } else {
                    BuyTrans.add(transaction);
                }
            }
            Collections.sort(BuyTrans, new RevTransDate_Dup());
            Collections.sort(SellTrans, new RevTransDate_Dup());
            final List<Trade> tradeList = new ArrayList<Trade>();
            for (final Transaction sellTrans : SellTrans) {
                findLIFOTrades(sellTrans, BuyTrans, tradeList);
            }
            printTrades(tradeList);
        }
    }
}
