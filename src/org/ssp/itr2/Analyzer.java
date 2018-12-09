package org.ssp.itr2;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

public class Analyzer {
    final public static SpDouble ZERO = new SpDouble();
    final transient private List<Transaction> aoCFTransactions = new ArrayList<Transaction>();
    final transient private List<Integer> aoCurrBuyTrans = new ArrayList<Integer>();
    final transient private List<Transaction> aoCurrentTrans = new ArrayList<Transaction>();
    final transient private List<Integer> aoCurrSellTrans = new ArrayList<Integer>();
    final transient private List<Integer> aoSortedBuyTrans = new ArrayList<Integer>();
    final transient private Map<String, ArrayList<Transaction>> moTransactions;

    public Analyzer(final BufferedReader bufReader, final int mode) {
        moTransactions = GenericUtilities.readLines(bufReader, mode);
    }

    private void addNewLTTrade(final List<Trade> longTermTrades, final Integer buyTransIndex,
            final Integer sellTransIndex) {
        longTermTrades.add(new Trade(buyTransIndex, sellTransIndex, Trade.LONGTERM, aoCurrentTrans));
    }

    public void analyze(final int iStartYear, final int iEndYear, final int iReportYear,
            final List<Trade> aoShortTermTrades, final List<Trade> aoLongTermTrades) {
        final Object[] asCodes = getFundEquityCodes();

        Arrays.sort(asCodes);
        for (final Object asCode : asCodes) {
            aoCFTransactions.clear();
            for (int iYearIndex = iStartYear; iYearIndex < iEndYear; iYearIndex++) {

                setcurrentTransactions((String) asCode, iYearIndex);

                if (!aoCurrentTrans.isEmpty()) {
                    // if (iYearIndex == iReportYear) {
                    // System.out.println(" - Transactions used - "
                    // + (String) asCodes[iIndex] + " - " + iYearIndex
                    // + " - ");
                    // double dSellQty = 0;
                    // for (int jIndex = 0; jIndex < aoCurrentTransactions
                    // .size(); jIndex++) {
                    // System.out.println(" "
                    // + aoCurrentTransactions.get(jIndex)
                    // .toString());
                    // if (!aoCurrentTransactions.get(jIndex).isBuy()) {
                    // dSellQty +=
                    // aoCurrentTransactions.get(jIndex).getAvailableQuantity().value();
                    // }
                    // }
                    // System.out.println(" - Total Sell Quantity to match - "
                    // + dSellQty);
                    // }

                    try {
                        findProfitableLongTermTrades(aoLongTermTrades);
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }

                    sortBuyTransactions();

                    findOptimizedTrades(aoShortTermTrades, aoLongTermTrades);
                    identifyCFTransactions();
                    if (iYearIndex == iReportYear) {
                        printTrades(aoShortTermTrades, aoLongTermTrades);
                    }
                }
                aoShortTermTrades.clear();
                aoLongTermTrades.clear();

            }
        }
    }

    private void createNewTrade(final Integer buyIndex, final Integer sellIndex, final Integer type,
            final List<Trade> shortTermTrades, final List<Trade> longTermTrades, final SpDouble tradeQty)
                    throws SITCException {
        final Trade oTrade = new Trade(buyIndex, sellIndex, type, aoCurrentTrans);
        oTrade.setQuantity(this, tradeQty, type);
        if (type == Trade.LONGTERM) {
            longTermTrades.add(oTrade);
        } else {
            shortTermTrades.add(oTrade);
        }
    }

    private void findMatchingBuys(final Integer iIndexArg, final List<Trade> aoShortTermTrades,
            final List<Trade> aoLongTermTrades) throws SITCException {

        final Transaction oSellTransaction = aoCurrentTrans.get(iIndexArg);
        final GregorianCalendar cal = new GregorianCalendar();
        final SpDouble dTradeQty = new SpDouble();
        for (int iIndex = 0; iIndex < aoSortedBuyTrans.size(); iIndex++) {

            final Integer iBuyIndex = aoSortedBuyTrans.get(iIndex);
            final Transaction oBuyTransaction = aoCurrentTrans.get(iBuyIndex);

            if (Trade.isTradeValid(oBuyTransaction, oSellTransaction)) {
                final Date dSellDate = oSellTransaction.getDate();
                final Date dBuyDate = oBuyTransaction.getDate();

                cal.setTime(dBuyDate);
                cal.add(Calendar.DATE, -1);
                cal.add(Calendar.YEAR, 1);
                final Date dDateYearAfterBuy = cal.getTime();

                Integer iType;
                if (dSellDate.before(dDateYearAfterBuy)) {
                    iType = Integer.valueOf(Trade.SHORTTERM);
                } else {
                    iType = Integer.valueOf(Trade.LONGTERM);
                }
                if (oSellTransaction.getAvailableQuantity().isGreatorThan(oBuyTransaction.getAvailableQuantity())) {
                    dTradeQty.set(oBuyTransaction.getAvailableQuantity());
                } else {
                    dTradeQty.set(oSellTransaction.getAvailableQuantity());
                }

                if (dTradeQty.isGreatorThan(ZERO)) {
                    createNewTrade(iBuyIndex, iIndexArg, iType, aoShortTermTrades, aoLongTermTrades, dTradeQty);
                }
            }

            if (oSellTransaction.getAvailableQuantity().isEqualTo(ZERO)) {
                break;
            }
        }

    }

    private void findOptimizedTrades(final List<Trade> aoShortTermTrades, final List<Trade> aoLongTermTrades) {

        for (int iIndex = 0; iIndex < aoCurrSellTrans.size(); iIndex++) {
            try {
                findMatchingBuys(aoCurrSellTrans.get(iIndex), aoShortTermTrades, aoLongTermTrades);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        System.out.flush();
    }

    private void findProfitableLongTermTrades(final List<Trade> aoLongTermTrades) throws SITCException {
        Transaction oBuyTransaction = null;
        Transaction oSellTransaction = null;
        Trade oTrade;
        SpDouble dTradeQty;
        final GregorianCalendar cal = new GregorianCalendar();

        for (int iIndex = 0; iIndex < aoCurrSellTrans.size(); iIndex++) {

            final Integer iSellTransIndex = aoCurrSellTrans.get(iIndex);
            oSellTransaction = aoCurrentTrans.get(iSellTransIndex.intValue());
            for (int jIndex = 0; jIndex < aoCurrBuyTrans.size(); jIndex++) {
                final Integer iBuyTransIndex = aoCurrBuyTrans.get(jIndex);
                oBuyTransaction = aoCurrentTrans.get(iBuyTransIndex.intValue());

                cal.setTime(oBuyTransaction.getDate());
                cal.add(Calendar.DATE, -1);
                cal.add(Calendar.YEAR, 1);
                final Date dDateYearAfterBuy = cal.getTime();

                if (oSellTransaction.getDate().after(dDateYearAfterBuy)) {
                    if (Trade.isAlreadyAdded(aoLongTermTrades, iBuyTransIndex, iSellTransIndex)) {
                        System.out.println("Ideally should never reach here!");
                    } else {
                        if (oSellTransaction.getRatePrice().isGreatorThan(oBuyTransaction.getRatePrice())) {
                            addNewLTTrade(aoLongTermTrades, iBuyTransIndex, iSellTransIndex);
                        }
                    }
                }
            }
        }

        Trade.sortTrades(aoLongTermTrades, aoCurrentTrans);

        final ArrayList<Trade> aoLTTrades = new ArrayList<Trade>();
        aoLTTrades.addAll(aoLongTermTrades);
        for (int iIndex = 0; iIndex < aoLTTrades.size(); iIndex++) {
            oTrade = aoLTTrades.get(iIndex);
            dTradeQty = Trade.validateLongTermTrade(aoCurrentTrans, oTrade);
            if (dTradeQty.isGreatorThan(ZERO)) {
                oTrade.setQuantity(this, dTradeQty, Trade.LONGTERM);
            } else {
                aoLongTermTrades.remove(oTrade);
            }
        }
    }

    private Object[] getFundEquityCodes() {
        return moTransactions.keySet().toArray();
    }

    private void identifyCFTransactions() {
        aoCFTransactions.clear();

        for (int iIndex = 0; iIndex < aoCurrentTrans.size(); iIndex++) {
            if (aoCurrentTrans.get(iIndex).getAvailableQuantity().isGreatorThan(ZERO)) {
                if (aoCurrentTrans.get(iIndex).isBuy()) {
                    aoCFTransactions.add(aoCurrentTrans.get(iIndex));
                } else {
                    System.out.println(" !Sell not satisfied : Qty = "
                            + aoCurrentTrans.get(iIndex).getAvailableQuantity() + " : " + aoCurrentTrans.get(iIndex));
                }
            }
        }
    }

    private void printTrades(final List<Trade> aoShortTermTrades, final List<Trade> aoLongTermTrades) {

        // double dLTTQty = 0.0;
        for (int iIndex = 0; iIndex < aoLongTermTrades.size(); iIndex++) {
            if (iIndex == 0) {
                System.out.println(" Long Term Trades");
            }
            aoLongTermTrades.get(iIndex).printTrade();
            // dLTTQty += aoLongTermTrades.get(iIndex).getQuantity().value();
        }

        // double dSTTQty = 0.0;
        if (aoShortTermTrades != null) {
            for (int iIndex = 0; iIndex < aoShortTermTrades.size(); iIndex++) {
                if (iIndex == 0) {
                    System.out.println(" Short Term Trades");
                }
                aoShortTermTrades.get(iIndex).printTrade();
                // dSTTQty +=
                // aoShortTermTrades.get(iIndex).getQuantity().value();
            }
        }
    }

    private void setcurrentTransactions(final String sCode, final int iYear) {
        Transaction oTransaction = null;
        final ArrayList<Transaction> aoTransactions = moTransactions.get(sCode);
        aoCurrentTrans.clear();

        // Add all carried forward buy transactions
        aoCurrentTrans.addAll(aoCFTransactions);
        aoCFTransactions.clear();

        // Get all buy and sell transaction in this year
        final GregorianCalendar cal = new GregorianCalendar();
        for (int iIndex = 0; iIndex < aoTransactions.size(); iIndex++) {
            oTransaction = aoTransactions.get(iIndex);

            cal.setTime(oTransaction.getDate());
            final int iTransMonth = cal.get(Calendar.MONTH);
            int iTransYear = 0;
            if (iTransMonth > Calendar.MARCH) {
                iTransYear = cal.get(Calendar.YEAR) + 1;
            } else {
                iTransYear = cal.get(Calendar.YEAR);
            }

            if (iTransYear == iYear) {
                aoCurrentTrans.add(oTransaction);
            }
        }

        Transaction.sort(aoCurrentTrans);
        aoCurrSellTrans.clear();
        aoCurrBuyTrans.clear();

        for (int iIndex = 0; iIndex < aoCurrentTrans.size(); iIndex++) {
            oTransaction = aoCurrentTrans.get(iIndex);

            if (oTransaction.isBuy()) {
                aoCurrBuyTrans.add(Integer.valueOf(iIndex));
            } else {
                aoCurrSellTrans.add(Integer.valueOf(iIndex));
            }
        }
    }

    private void sortBuyTransactions() {
        Transaction oExpensiveBuy = null;
        int iExpensiveIndex;
        aoSortedBuyTrans.clear();

        final ArrayList<Integer> aoBuyTransCopy = new ArrayList<Integer>();
        aoBuyTransCopy.addAll(aoCurrBuyTrans);
        boolean resetExpBuy = true;
        while (!aoBuyTransCopy.isEmpty()) {
            resetExpBuy = true;
            iExpensiveIndex = 0;
            for (int iIndex = 0; iIndex < aoBuyTransCopy.size(); iIndex++) {
                final Integer iBuyIndex = aoBuyTransCopy.get(iIndex);

                if (resetExpBuy) {
                    resetExpBuy = false;
                    oExpensiveBuy = aoCurrentTrans.get(iBuyIndex);
                } else {
                    final Transaction oBuyTrans = aoCurrentTrans.get(iBuyIndex);

                    if (oBuyTrans.getRatePrice().isGreatorThan(oExpensiveBuy.getRatePrice())) {
                        oExpensiveBuy = oBuyTrans;
                        iExpensiveIndex = iIndex;
                    }
                }
            }

            aoSortedBuyTrans.add(aoBuyTransCopy.remove(iExpensiveIndex));
        }
    }

    public void spawnIfRequired(final Trade trade) throws SITCException {
        final Transaction oBuyTrans = aoCurrentTrans.get(trade.getBuyIndex());
        final Transaction oSellTrans = aoCurrentTrans.get(trade.getSellIndex());

        if (oBuyTrans.getAvailableQuantity().isGreatorThan(trade.getQuantity())) {
            oBuyTrans.spawnTransaction(trade);
        } else {
            oBuyTrans.handleTradeEffect(trade);
        }

        if (oSellTrans.getAvailableQuantity().isGreatorThan(trade.getQuantity())) {
            oSellTrans.spawnTransaction(trade);
        } else {
            oSellTrans.handleTradeEffect(trade);
        }
    }

}
