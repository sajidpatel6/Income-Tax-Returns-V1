package org.ssp.tools.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Transaction {

    public static final DecimalFormat DECIMAL_FORMAT = (DecimalFormat) NumberFormat.getInstance();

    private static final Logger LOGGER = Logger.getLogger(Transaction.class.getName());

    static {
        DECIMAL_FORMAT.setGroupingUsed(false);
    }

    public static HashSet<Transaction> getAllTransactions(final File transFile)
            throws FileNotFoundException, IOException {
        final HashSet<Transaction> transactions = new HashSet<Transaction>();

        if ((transFile != null) && transFile.exists()) {
            final BufferedReader quoteReader = new BufferedReader(new FileReader(transFile));
            String str = quoteReader.readLine();
            if (str != null) {
                str = quoteReader.readLine(); // skip the first line
            }

            while (str != null) {
                boolean skipBhavQuote = false;
                final StringTokenizer tokenizer = new StringTokenizer(str, ",");
                if (tokenizer.countTokens() > 12) {
                    System.out.println(str + "  " + tokenizer.countTokens());
                }
                Transaction transaction = null;
                try {
                    transaction = new Transaction(tokenizer);
                } catch (final UnsupportedOperationException uoe) {
                    LOGGER.log(Level.INFO, "Quote date does not match");
                    skipBhavQuote = true;
                }
                if (!skipBhavQuote) {
                    transactions.add(transaction);
                }

                str = quoteReader.readLine();
            }
            quoteReader.close();
        }
        return transactions;
    }

    private String action;

    private BigDecimal balQuantity = BigDecimal.valueOf(-1);

    private BigDecimal brokerageTax;

    private Date date;

    private BigDecimal price;

    private BigDecimal quantity;

    private String symbol;

    Transaction(final String[] record) {
        date = AbstractDateUtil.dateParse(record[0], AbstractDateUtil.DDMMMYYYY);
        symbol = record[1];
        action = record[2];
        quantity = new BigDecimal(record[3]);
        balQuantity = new BigDecimal(record[3]);
        price = new BigDecimal(record[4]);
        brokerageTax = new BigDecimal(record[5]);
    }

    private Transaction(final StringTokenizer tokenizer) {
        setValues(tokenizer, null);
    }

    public String getAction() {
        return action;
    }

    public BigDecimal getBalQuantity() {
        return balQuantity;
    }

    Date getDate() {
        return (Date) date.clone();
    }

    BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public String getSymbol() {
        return symbol;
    }

    String printForTrade() {
        final SimpleDateFormat SDF = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        final StringBuilder strBuilder = new StringBuilder(SDF.format(date));
        strBuilder.append(',');
        strBuilder.append(symbol);
        strBuilder.append(',');
        strBuilder.append(action);
        strBuilder.append(',');
        strBuilder.append(DECIMAL_FORMAT.format(quantity));
        strBuilder.append(',');
        strBuilder.append(DECIMAL_FORMAT.format(price));
        strBuilder.append(',');
        strBuilder.append(DECIMAL_FORMAT.format(brokerageTax));

        return strBuilder.toString();
    }

    void reduceBalQuantity(final BigDecimal balQuantity2) {
        balQuantity = balQuantity.subtract(balQuantity2);
    }

    public void setBalQuantity(final BigDecimal balQuantity) {
        this.balQuantity = balQuantity;
    }

    private void setValues(final StringTokenizer tokenizer, final Date transDate) {
        // 0.StockSymbol,1.Action,2.Quantity,3.TransactionPrice,4.Brokerage,5.TransactionCharges,6.StampDuty,7.Segment,8.STTPaid/NotPaid,9.Remarks,10.TransactionDate,11.Exchange
        int index = 0;
        if (transDate != null) {
            date = (Date) transDate.clone();
        }
        while (tokenizer.hasMoreTokens()) {
            final String nextToken = tokenizer.nextToken().trim();
            switch (index) {
            case 0: // 0.StockSymbol
                symbol = nextToken;
                break;
            case 1: // 1.Action
                action = nextToken;
                break;
            case 2: // 2.Quantity
                quantity = new BigDecimal(nextToken);
                balQuantity = new BigDecimal(nextToken);
                break;
            case 3: // 3.TransactionPrice
                price = new BigDecimal(nextToken);
                break;
            case 4: // 4.Brokerage
                brokerageTax = new BigDecimal(nextToken);
                break;
            case 5: // 5.TransactionCharges
                if (!"NA".equals(nextToken)) {
                    final BigDecimal transCharges = new BigDecimal(nextToken);
                    if (transCharges.doubleValue() > 0) {
                        brokerageTax = brokerageTax.add(transCharges);
                    }
                }
                break;
            case 6: // 6.StampDuty
                if (!"NA".equals(nextToken)) {
                    final BigDecimal stampDuty = new BigDecimal(nextToken);

                    if (stampDuty.doubleValue() > 0) {
                        brokerageTax = brokerageTax.add(stampDuty);
                    }
                }
                break;
            case 7: // 7.Segment
                break;
            case 8: // 8.STTPaid/NotPaid
                break;
            case 9: // 9.Remarks
                break;
            case 10: // 10.TransactionDate
                if (transDate == null) {
                    date = AbstractDateUtil.dateParse(nextToken, AbstractDateUtil.DDMMMYYYY);
                } else {
                    final SimpleDateFormat SDF = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                    final String dateStr = SDF.format(date);
                    if (!dateStr.equals(nextToken)) {
                        throw new UnsupportedOperationException("Not the right date");
                    }
                }
                break;
            case 11: // 11.Exchange
                break;
            default:
                break;
            }
            index++;
        }
    }

    @Override
    public String toString() {
        final SimpleDateFormat SDF = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        final StringBuilder strBuilder = new StringBuilder(SDF.format(date));
        strBuilder.append(',');
        strBuilder.append(symbol);
        strBuilder.append(',');
        strBuilder.append(action);
        strBuilder.append(',');
        strBuilder.append(DECIMAL_FORMAT.format(quantity));
        strBuilder.append(',');
        strBuilder.append(DECIMAL_FORMAT.format(price));
        strBuilder.append(',');
        strBuilder.append(DECIMAL_FORMAT.format(brokerageTax));

        if ("BUY".equalsIgnoreCase(action) && (quantity.compareTo(balQuantity) == 1)
                && (balQuantity.compareTo(BigDecimal.valueOf(-1)) != 0)) {
            strBuilder.append(" -> ");
            strBuilder.append(DECIMAL_FORMAT.format(balQuantity));
        }

        return strBuilder.toString();
    }
}
