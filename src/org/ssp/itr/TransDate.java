package org.ssp.itr;

import java.util.Comparator;

public class TransDate implements Comparator<Transaction> {

    @Override
    public int compare(final Transaction o1, final Transaction o2) {
        int returnVal = o1.getDate().compareTo(o2.getDate());

        if (returnVal == 0) {
            returnVal = o2.getQuantity().compareTo(o1.getQuantity());
            if ("BUY".equalsIgnoreCase(o1.getAction())) {
                if ("SELL".equalsIgnoreCase(o2.getAction())) {
                    returnVal = -1;
                }
            } else {
                if ("BUY".equalsIgnoreCase(o2.getAction())) {
                    returnVal = 1;
                }
            }
        }
        if (returnVal == 0) {
            returnVal = o2.getPrice().compareTo(o1.getPrice());
        }
        return returnVal;
    }
}
