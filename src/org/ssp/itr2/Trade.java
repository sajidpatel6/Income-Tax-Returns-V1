package org.ssp.itr2;

import java.util.ArrayList;
import java.util.List;

public class Trade {
    public final static int LONGTERM = 2;

    public final static int SHORTTERM = 1;

    public final static int UNSET = 0;

    public static boolean isAlreadyAdded(final List<Trade> aoLongTermTrades, final Integer buyTransIndex,
            final Integer sellTransIndex) {
        Trade oTrade;

        boolean isFound = false;
        for (int iIndex = 0; iIndex < aoLongTermTrades.size(); iIndex++) {

            oTrade = aoLongTermTrades.get(iIndex);
            if ((oTrade.iBuyIndex == buyTransIndex.intValue()) && (oTrade.iSellIndex == sellTransIndex.intValue())) {
                System.out.println("Unexpected - Identical Trade!");
                isFound = true;
                break;
            }
        }
        return isFound;
    }

    public static boolean isTradeValid(final Transaction oBuyTrans, final Transaction oSellTrans) {
        boolean returnFlag = false;

        if (oBuyTrans.getStockCode().matches(oSellTrans.getStockCode()) && oBuyTrans.isBuy() && !oSellTrans.isBuy()
                && !oSellTrans.getDate().before(oBuyTrans.getDate())) {
            // If the Sell is on the same day as Buy or after Buy
            returnFlag = true;
        }
        return returnFlag;
    }

    public static void sortTrades(final List<Trade> aoLongTermTrades, final List<Transaction> aoCurrTransArg) {

        Trade oMaxProfitTrade = null;
        boolean needNewMPT = true;
        Trade oTrade2Analyze = null;
        Transaction oBuyTrans = null;
        Transaction oSellTrans = null;
        Transaction oMaxProfitBuy = null;
        Transaction oMaxProfitSell = null;

        final ArrayList<Trade> aoTradesCopy = new ArrayList<Trade>();
        aoTradesCopy.addAll(aoLongTermTrades);
        aoLongTermTrades.clear();
        final SpDouble oGap2Analyze = new SpDouble();
        final SpDouble oGapMaxProfit = new SpDouble();

        while (!aoTradesCopy.isEmpty()) {
            for (int iIndex = 0; iIndex < aoTradesCopy.size(); iIndex++) {
                if (needNewMPT) {
                    needNewMPT = false;
                    oMaxProfitTrade = aoTradesCopy.get(iIndex);
                } else {
                    oTrade2Analyze = aoTradesCopy.get(iIndex);

                    oMaxProfitBuy = aoCurrTransArg.get(oMaxProfitTrade.iBuyIndex);
                    oMaxProfitSell = aoCurrTransArg.get(oMaxProfitTrade.iSellIndex);
                    oBuyTrans = aoCurrTransArg.get(oTrade2Analyze.iBuyIndex);
                    oSellTrans = aoCurrTransArg.get(oTrade2Analyze.iSellIndex);

                    oGap2Analyze.set(oSellTrans.getRatePrice());
                    oGapMaxProfit.set(oMaxProfitSell.getRatePrice());
                    oGap2Analyze.subtract(oBuyTrans.getRatePrice());
                    oGapMaxProfit.subtract(oMaxProfitBuy.getRatePrice());
                    if (oGap2Analyze.isGreatorThan(oGapMaxProfit)) {
                        oMaxProfitTrade = oTrade2Analyze;
                    }
                }
            }
            aoTradesCopy.remove(oMaxProfitTrade);

            aoLongTermTrades.add(oMaxProfitTrade);
            needNewMPT = true;
        }
    }

    public static SpDouble validateLongTermTrade(final List<Transaction> aoCurrTransArg, final Trade trade) {
        final SpDouble oLowestNetQty = new SpDouble();
        final SpDouble oNetQty = new SpDouble();
        final SpDouble oLongTermQuantity = new SpDouble();

        final Transaction oBuyTrans = aoCurrTransArg.get(trade.iBuyIndex);
        final Transaction oSellTrans = aoCurrTransArg.get(trade.iSellIndex);

        // The logic to validate long term trade is as follows:
        // Start calculating Net Balance quantity starting from the first
        // transaction. The lowest net quantity that we find at the point
        // when sell transaction is reached is the eligible long term trade
        // quantity.
        for (int jIndex = 0; jIndex < trade.iSellIndex; jIndex++) {

            if (trade.iBuyIndex == jIndex) {
                continue;
            }

            final Transaction oTrans2Analyze = aoCurrTransArg.get(jIndex);

            if (oTrans2Analyze.isBuy()) {
                oNetQty.add(oTrans2Analyze.getAvailableQuantity());
            } else {
                oNetQty.subtract(oTrans2Analyze.getAvailableQuantity());
            }

            if (oLowestNetQty.isGreatorThan(oNetQty)) {
                oLowestNetQty.set(oNetQty);
            }

            final SpDouble oNegLowestNetQty = new SpDouble();
            oNegLowestNetQty.subtract(oLowestNetQty);
            if (oNegLowestNetQty.isGreatorThan(oBuyTrans.getAvailableQuantity())) {
                System.out.println("Mostly invalid scenario, please verify!");
                break;
            }
        }

        final SpDouble oNegBuyQty = new SpDouble();
        oNegBuyQty.subtract(oBuyTrans.getAvailableQuantity());

        if (oLowestNetQty.isGreatorThan(oNegBuyQty)) {
            final SpDouble oQualifyingQty = new SpDouble();
            if (oLowestNetQty.equals(new SpDouble()) || oLowestNetQty.isGreatorThan(new SpDouble())) {
                oQualifyingQty.set(oBuyTrans.getAvailableQuantity());
            } else {
                oQualifyingQty.set(oBuyTrans.getAvailableQuantity());
                oQualifyingQty.add(oLowestNetQty);
            }

            if (oQualifyingQty.isGreatorThan(oSellTrans.getAvailableQuantity())) {
                oLongTermQuantity.set(oSellTrans.getAvailableQuantity());
            } else {
                oLongTermQuantity.set(oQualifyingQty);
            }
        }

        return oLongTermQuantity;
    }

    final transient private List<Transaction> aoCurrentTrans;

    final transient private int iBuyIndex;

    final transient private int iSellIndex;

    transient private int iType = Trade.UNSET;

    final transient private SpDouble oQuantity = new SpDouble();

    public Trade(final Integer buyIndexArg, final Integer sellIndexArg, final int iTypeArg,
            final List<Transaction> aoCurrentTransArg) {
        iBuyIndex = buyIndexArg;
        iSellIndex = sellIndexArg;
        iType = iTypeArg;
        oQuantity.set(-1);
        aoCurrentTrans = aoCurrentTransArg;
    }

    public Trade(final Integer buyIndexArg, final Integer sellIndexArg, final List<Transaction> aoCurrTrans) {
        iBuyIndex = buyIndexArg;
        iSellIndex = sellIndexArg;
        oQuantity.set(-1);
        iType = Trade.UNSET;
        aoCurrentTrans = aoCurrTrans;
    }

    public int getBuyIndex() {
        return iBuyIndex;
    }

    public Transaction getBuyTrans() {
        return aoCurrentTrans.get(iBuyIndex);
    }

    public SpDouble getQuantity() {
        return oQuantity;
    }

    public int getSellIndex() {
        return iSellIndex;
    }

    public Transaction getSellTrans() {
        return aoCurrentTrans.get(iSellIndex);
    }

    public int getType() {
        return iType;
    }

    public void printTrade() {
        final Transaction oBuyTrans = aoCurrentTrans.get(getBuyIndex());
        final Transaction oSellTrans = aoCurrentTrans.get(getSellIndex());

        System.out.println("  " + oBuyTrans.toString(getQuantity()));
        System.out.println("  " + oSellTrans.toString(getQuantity()));
    }

    public void setQuantity(final Analyzer analyzer, final SpDouble tradeQty, final Integer type) throws SITCException {
        if (iType != type) {
            System.out.println("Unexpected term trade!");
        }
        oQuantity.set(tradeQty);

        analyzer.spawnIfRequired(this);

        // analyzer.printTrade(this);
    }

    public void setType(final int type) {
        iType = type;
    }

}
