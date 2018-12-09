package org.ssp.itr;

import java.util.Comparator;

public class RevTransDate_Dup implements Comparator<Transaction> {
    @Override
    public int compare(final Transaction o1, final Transaction o2) {
        int returnVal = o2.getDate().compareTo(o1.getDate());

        if (returnVal == 0) {
            returnVal = o1.getQuantity().compareTo(o2.getQuantity());
            if ("BUY".equalsIgnoreCase(o2.getAction())) {
                if ("SELL".equalsIgnoreCase(o1.getAction())) {
                    returnVal = -1;
                }
            } else {
                if ("BUY".equalsIgnoreCase(o1.getAction())) {
                    returnVal = 1;
                }
            }
        }
        if (returnVal == 0) {
            returnVal = o1.getPrice().compareTo(o2.getPrice());
        }
        return returnVal;
    }
}
