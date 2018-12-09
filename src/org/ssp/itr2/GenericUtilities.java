package org.ssp.itr2;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class GenericUtilities {

    public static final DecimalFormat FOUR_DECIMAL = new DecimalFormat("########.####");

    public static final DecimalFormat QUANTITY_DECIMAL = new DecimalFormat("#######.##");

    public static final DecimalFormat TWO_DECIMAL = new DecimalFormat("########.##");

    public static Map<String, ArrayList<Transaction>> readLines(final BufferedReader bufReader, final int mode) {
        boolean bProcess = true;
        boolean bFirstLine = true;
        final HashMap<String, ArrayList<Transaction>> moTransactions = new HashMap<String, ArrayList<Transaction>>();

        String sLine;
        try {
            while (bProcess) {
                sLine = bufReader.readLine();

                if (sLine == null) {
                    bProcess = false;
                    continue;
                }

                if (bFirstLine) {
                    bFirstLine = false;
                    continue;
                }

                final String[] sRecord = sLine.split(",");

                final boolean bIsValid = validate(sRecord, mode);
                if (!bIsValid) {
                    System.out.println(" Sajid - Problem Record - " + sLine);
                    continue;
                }

                final Transaction oTransaction = new Transaction(sRecord, mode);

                ArrayList<Transaction> aoTransactions;
                if (moTransactions.containsKey(oTransaction.getStockCode())) {
                    aoTransactions = moTransactions.get(oTransaction.getStockCode());
                } else {
                    aoTransactions = new ArrayList<Transaction>();
                }

                aoTransactions.add(oTransaction);
                moTransactions.put(oTransaction.getStockCode(), aoTransactions);
            }
        } catch (final IOException ioe) {
            ioe.printStackTrace();
        } catch (final Exception e) {
            e.printStackTrace();
        }

        return moTransactions;

    }

    private static boolean validate(final String[] record, final int mode) {
        boolean bIsValid = true;

        int iValidRecordCount = 0;
        if (mode == IncomeTaxCalc.EQUITIES_MODE) {
            iValidRecordCount = 7;
        } else if (mode == IncomeTaxCalc.MUTUALFUNDS_MODE) {
            iValidRecordCount = 12;
            if ("NA".matches(record[5]) || "NA".matches(record[8])) {
                bIsValid = false;
            }
        }
        if (record.length != iValidRecordCount) {
            bIsValid = false;
        }

        return bIsValid;
    }

    private GenericUtilities() {
    }
}
