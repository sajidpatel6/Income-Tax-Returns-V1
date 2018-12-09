package org.ssp.itr2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public final class IncomeTaxCalc {

    public static final int EQUITIES_MODE = 1;
    private static int iEndYear = 2011; // End Assessment year
    private static int iReportYear = iEndYear - 1;
    private static int iStartYear = 2004;
    public static final int MUTUALFUNDS_MODE = 2;

    public static void main(final String[] args) {
        processCGforITReturn("ITData.csv", IncomeTaxCalc.EQUITIES_MODE);
        processCGforITReturn("ITDataMF.csv", IncomeTaxCalc.MUTUALFUNDS_MODE);
    }

    public static void processCGforITReturn(final String sFileName, final int iMode) {
        /*
         * Tool to calculate a report for income tax filing The primary
         * objective is to minimize the tax liability This will be achieved by
         * this approach
         */

        // Step 1 : Find all the sell transactions in the current year
        // Step 2 : Find all the qualifying long term trades
        // Step 3 : Find all possible long term trades with profits
        // (ignore long term trades with losses)
        // Step 4 : Match trades from remaining transactions to minimize profits
        // or maximize losses. Minimize tax liability i.e. minimize
        // short term profit
        // Step 5 : The above steps will also identify trades to carry forward
        // for the next financial year.
        // Step 6 : Generate the following reports
        // a. Long term trades
        // b. Short term trades
        // c. Trades that can be discarded from next year onwards
        // d. Carried forward trades
        try {
            final BufferedReader bufReader = new BufferedReader(new FileReader(sFileName));

            final Analyzer sitcAnalyzer = new Analyzer(bufReader, iMode);

            final ArrayList<Trade> aoShortTermTrades = new ArrayList<Trade>();
            final ArrayList<Trade> aoLongTermTrades = new ArrayList<Trade>();

            sitcAnalyzer.analyze(iStartYear, iEndYear, iReportYear, aoShortTermTrades, aoLongTermTrades);

            bufReader.close();
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private IncomeTaxCalc() {
        // Private Constructor
    }

}
