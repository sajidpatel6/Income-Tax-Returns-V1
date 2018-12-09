package org.ssp.temp.fm.itr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ssp.tools.util.TransDate;
import org.ssp.tools.util.Transaction;
import org.ssp.tools.util.StaticStrings;

public class MainClass {

    public static void main(final String[] args) {
        try {
            final HashSet<Transaction> transSet = Transaction.getAllTransactions(new File(StaticStrings.FILE_EQ_TRANS));

            // System.out.println(" Found " + transSet.size() +
            // " transactions!");

            final Map<String, List<Transaction>> stockTransMap = new HashMap<String, List<Transaction>>();

            for (final Transaction transaction : transSet) {
                // System.out.println(transaction);
                if (stockTransMap.containsKey(transaction.getSymbol())) {
                    final List<Transaction> transList = stockTransMap.get(transaction.getSymbol());
                    transList.add(transaction);
                } else {
                    final List<Transaction> transList = new ArrayList<Transaction>();
                    transList.add(transaction);
                    stockTransMap.put(transaction.getSymbol(), transList);
                }
            }

            for (final Entry<String, List<Transaction>> entry : stockTransMap.entrySet()) {
                // final String stockSymbol = entry.getKey();
                final List<Transaction> transList = entry.getValue();
                // System.out.println(" -- " + stockSymbol + " has "
                // + transList.size() + " transactions.");

                Collections.sort(transList, new TransDate());
            }

            FIFOMatcher.process(stockTransMap);
            LIFOMatcher.process(stockTransMap);
            MaxLTPMaxSTLMatcher.process(stockTransMap);
            MaxSTLMaxLTPMatcher.process();
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

}
