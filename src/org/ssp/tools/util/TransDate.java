package org.ssp.tools.util;

import java.util.Comparator;

public class TransDate implements Comparator<Transaction> {

    @Override
    public int compare(final Transaction obj1, final Transaction obj2) {
        int returnVal = obj1.getDate().compareTo(obj2.getDate());

        if (returnVal == 0) {
            returnVal = obj2.getQuantity().compareTo(obj1.getQuantity());
            if ("BUY".equalsIgnoreCase(obj1.getAction())) {
                if ("SELL".equalsIgnoreCase(obj2.getAction())) {
                    returnVal = -1;
                }
            } else {
                if ("BUY".equalsIgnoreCase(obj2.getAction())) {
                    returnVal = 1;
                }
            }
        }
        if (returnVal == 0) {
            returnVal = obj2.getPrice().compareTo(obj1.getPrice());
        }
        return returnVal;
    }
}
