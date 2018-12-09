package org.ssp.tools.util;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Trade {
    private final static int LONGTERM = 2;

    private final static int SHORTTERM = 1;

    private final static int UNSET = 0;

    transient private int iType = Trade.UNSET;

    private final transient BigDecimal tradeQty;

    private final transient Transaction buyTransaction;

    private final transient Transaction sellTransaction;

    public Trade(final Transaction buyTrans, final Transaction sellTrans, final BigDecimal tradeQuantity) {
        if (tradeQuantity.compareTo(BigDecimal.ZERO) == 0) {
            System.out.println("Severe error!! zero size trade!");
        }

        buyTransaction = buyTrans;
        sellTransaction = sellTrans;
        tradeQty = tradeQuantity;

        final GregorianCalendar gcal = new GregorianCalendar();
        gcal.setTime(sellTrans.getDate());
        gcal.add(Calendar.YEAR, -1);
        gcal.add(Calendar.DAY_OF_YEAR, 1);
        final Date dateYearBack = gcal.getTime();
        // System.out.println(SDF.format(sellTrans.getDate()) + " -- "
        // + SDF.format(dateYearBack));
        if (buyTrans.getDate().before(dateYearBack)) {
            iType = LONGTERM;
        } else {
            iType = SHORTTERM;
        }

        if (buyTrans.getBalQuantity().compareTo(tradeQuantity) >= 0) {
            buyTrans.reduceBalQuantity(tradeQuantity);
            // System.out.println("B-> " + buyTrans + " Bal reduced by "
            // + tradeQuantity + " - " + buyTrans.getBalQuantity());
        }

        if (sellTrans.getBalQuantity().compareTo(tradeQuantity) >= 0) {
            sellTrans.reduceBalQuantity(tradeQuantity);
            // System.out.println("S-> " + sellTrans + " Bal reduced by "
            // + tradeQuantity + " - " + sellTrans.getBalQuantity());
        }
    }

    public void print() {
        System.out.println(tradeQty + "   B: " + buyTransaction.printForTrade() + "   S: "
                + sellTransaction.printForTrade() + (iType == LONGTERM ? "   LT" : "   ST"));
    }
}
