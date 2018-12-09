package org.ssp.itr2;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Transaction {

    public static final String BUY_ACTION = "Buy";

    public static final String SELL_ACTION = "Sell";

    public static void sort(final List<Transaction> aoCurrentTrans) {
        final ArrayList<Transaction> copiedTrans = new ArrayList<Transaction>(aoCurrentTrans);

        aoCurrentTrans.clear();
        Transaction oQualifyingTrans;
        Transaction oTrans2Analyze;

        while (!copiedTrans.isEmpty()) {
            // Get the first record
            oQualifyingTrans = copiedTrans.get(0);

            for (int iIndex = 1; iIndex < copiedTrans.size(); iIndex++) {
                oTrans2Analyze = copiedTrans.get(iIndex);
                if (oTrans2Analyze.getDate().before(oQualifyingTrans.getDate())) {
                    oQualifyingTrans = oTrans2Analyze;
                } else if ((!oTrans2Analyze.getDate().after(oQualifyingTrans.getDate()))
                        && (oTrans2Analyze.isBuy() && !oQualifyingTrans.isBuy())) {
                    oQualifyingTrans = oTrans2Analyze;
                }
            }
            copiedTrans.remove(oQualifyingTrans);
            aoCurrentTrans.add(oQualifyingTrans);
        }

    }

    transient private List<Trade> aoRefTrades = null;

    transient private boolean bLongTermFlag = false;

    transient private Date dDate = null;

    transient private double dTradeValue = 0;

    transient private SpDouble oAvailableQty = new SpDouble();

    transient private SpDouble oBrokerageTaxes = new SpDouble();

    transient private SpDouble oQuantity = new SpDouble();

    transient private SpDouble oRatePrice = new SpDouble();

    transient private String sAction = null;

    transient private String sStockCode = null;

    public Transaction(final String sRecord[], final int mode) throws SITCException {

        try {
            if (mode == IncomeTaxCalc.EQUITIES_MODE) {
                final SimpleDateFormat DD_MMM_YY = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
                dDate = DD_MMM_YY.parse(sRecord[0]);
                sStockCode = sRecord[1];
                sAction = sRecord[2];

                oAvailableQty = new SpDouble(sRecord[3]);
                oQuantity = new SpDouble(sRecord[3]);

                oRatePrice = new SpDouble(sRecord[4]);
                oBrokerageTaxes = new SpDouble(sRecord[6]);
            }

            if (mode == IncomeTaxCalc.MUTUALFUNDS_MODE) {
                final SimpleDateFormat DD_MM_YYYY = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

                dDate = DD_MM_YYYY.parse(sRecord[0]);
                sStockCode = sRecord[4];
                if ("P".matches(sRecord[2]) || "DR".matches(sRecord[2])) {
                    sAction = BUY_ACTION;
                } else if ("R".matches(sRecord[2])) {
                    sAction = SELL_ACTION;
                } else {
                    throw new SITCException("Action not detected");
                }
                oAvailableQty = new SpDouble(sRecord[9]);
                oQuantity = new SpDouble(sRecord[9]);
                oRatePrice = new SpDouble(sRecord[5]);
                oBrokerageTaxes.set(0);
            }

            bLongTermFlag = false;
            dTradeValue = oRatePrice.value() * oQuantity.value();
        } catch (final Exception e) {
            throw new SITCException(e);
        }
    }

    Transaction(final Transaction oTransaction) {
        dDate = (Date) oTransaction.dDate.clone();
        sStockCode = oTransaction.sStockCode;
        sAction = oTransaction.sAction;
        oAvailableQty = new SpDouble(oTransaction.oAvailableQty);
        oQuantity = new SpDouble(oTransaction.oQuantity);
        oRatePrice = new SpDouble(oTransaction.oRatePrice);
        oBrokerageTaxes = new SpDouble(oTransaction.oBrokerageTaxes);

        bLongTermFlag = oTransaction.bLongTermFlag;
        dTradeValue = oRatePrice.value() * oQuantity.value();
    }

    public SpDouble getAvailableQuantity() {
        return oAvailableQty;
    }

    public double getBrokerageTaxesPerUnit() {
        return oBrokerageTaxes.value() / oQuantity.value();
    }

    public Date getDate() {
        return dDate;
    }

    public SpDouble getRatePrice() {
        return oRatePrice;
    }

    public String getStockCode() {
        return sStockCode;
    }

    public double getTradeValue() {
        return dTradeValue;
    }

    public void handleTradeEffect(final Trade trade) {
        if (aoRefTrades == null) {
            aoRefTrades = new ArrayList<Trade>();
        }
        oAvailableQty.set(0);
        aoRefTrades.add(trade);
    }

    public boolean isBuy() {
        return BUY_ACTION.matches(sAction);
    }

    public void spawnTransaction(final Trade trade) throws SITCException {
        if (aoRefTrades == null) {
            aoRefTrades = new ArrayList<Trade>();
        }

        if (trade.getQuantity().isGreatorThan(oAvailableQty)) {
            System.out.println("Severe error!");
            throw new SITCException("Available quantity less than trade quantity");
        }

        oAvailableQty.subtract(trade.getQuantity());
        aoRefTrades.add(trade);
    }

    @Override
    public String toString() {
        final SimpleDateFormat DD_MMM_YYYY = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        return (DD_MMM_YYYY.format(dDate) + "," + sStockCode + "," + sAction + ","
                + (BUY_ACTION.matches(sAction) ? "" : "-") + oQuantity + "," + oRatePrice + ","
                + GenericUtilities.TWO_DECIMAL.format((BUY_ACTION.matches(sAction) ? -1 : 1) * dTradeValue) + "," + "-"
                + oBrokerageTaxes
        // + " --- " + (BUY_ACTION.matches(sAction) ? "" : "-") + oAvailableQty
        );
    }

    public String toString(final SpDouble dQty) {
        final SimpleDateFormat DD_MMM_YYYY = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        return (DD_MMM_YYYY.format(dDate) + "," + sStockCode + "," + sAction + ","
                + (BUY_ACTION.matches(sAction) ? "" : "-") + dQty + "," + oRatePrice + ","
                + GenericUtilities.TWO_DECIMAL
                        .format((BUY_ACTION.matches(sAction) ? -1 : 1) * dQty.value() * oRatePrice.value())
                + ","
                + GenericUtilities.TWO_DECIMAL.format((-dQty.value() * oBrokerageTaxes.value()) / oQuantity.value())
        // + " --- " + (BUY_ACTION.matches(sAction) ? "" : "-") + oAvailableQty
        );
    }

}
