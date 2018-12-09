package org.ssp.itr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MaxLTPMaxSTLMatcher {

    private static Map<String, List<Transaction>> filterTransForYear(final int iYearIndex,
            final Map<String, List<Transaction>> stockTransMap) {
        // TODO Auto-generated method stub
        return stockTransMap;
    }

    private static void findEligibleLTTrades(final Map<String, List<Transaction>> stockTransMap) {
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
            Collections.sort(BuyTrans, new TransDate());
            Collections.sort(SellTrans, new TransDate());
            // final List<Trade> tradeList = new ArrayList<Trade>();
            for (final Transaction sellTrans : SellTrans) {
                System.out.println(sellTrans);
            }
            // printTrades(tradeList);
        }

    }

    private static void findEligibleSTTrades() {
        // TODO Auto-generated method stub

    }

    private static void matchAnyRemaing() {
        // TODO Auto-generated method stub

    }

    public static void process(final Map<String, List<Transaction>> stockTransMap) {

        // 1. Start with first financial year
        // 2. Take buy and sell transactions in that year plus carry forward buy
        // transactions (for the first year there are no carry forward
        // transactions)
        // 3.Iterate through financial years one by one and find trades
        for (int iYearIndex = 2004; iYearIndex < 2006; iYearIndex++) {
            final Map<String, List<Transaction>> yearSTMap = filterTransForYear(iYearIndex, stockTransMap);
            findEligibleLTTrades(yearSTMap);
            validateAndFinalizeLTT();
            findEligibleSTTrades();
            validateAndFinalizeSTT();
            matchAnyRemaing();
        }
    }

    private static void validateAndFinalizeLTT() {
        // TODO Auto-generated method stub

    }

    private static void validateAndFinalizeSTT() {
        // TODO Auto-generated method stub

    }

}
