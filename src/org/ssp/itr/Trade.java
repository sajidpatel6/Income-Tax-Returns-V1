package org.ssp.itr;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Trade {
    public final static int LONGTERM = 2;

    public final static int SHORTTERM = 1;

    public final static int UNSET = 0;

    Transaction buyTransaction = null;

    transient private int iType = Trade.UNSET;

    Transaction sellTransaction = null;

    transient private BigDecimal tradeQty = BigDecimal.ZERO;

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
