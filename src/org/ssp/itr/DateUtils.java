package org.ssp.itr;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class DateUtils {
    public enum MONTH {
        JAN, FEB, MAR, APR, MAY, JUN, JUL, AUG, SEP, OCT, NOV, DEC
    }

    private final static Map<String, Calendar> DATE_CACHE = new HashMap<String, Calendar>();;

    static final String DDMMMYYYY = "dd-MMM-yyyy";

    static Date dateParse(final String dateStr, final String format) {
        Date date = null;
        if (DDMMMYYYY.equals(format)) {
            date = ddMMMyyyyParse(dateStr);
        } else {
            // TODO other formats
        }
        return date;
    }

    // Parses dd-MMM-yyyy
    private static Date ddMMMyyyyParse(final String dateStr) {
        Calendar cal = null;
        if (DATE_CACHE.containsKey(dateStr)) {
            cal = DATE_CACHE.get(dateStr);
        } else {
            final StringTokenizer stringTok = new StringTokenizer(dateStr, "-");
            int dayOfMonth = -1;
            int month = -1;
            int year = -1;
            int index = 0;
            while (stringTok.hasMoreTokens()) {
                final String nextToken = stringTok.nextToken();
                switch (index) {
                case 0:
                    dayOfMonth = Integer.parseInt(nextToken);
                    break;
                case 1:
                    month = MONTH.valueOf(nextToken.toUpperCase()).ordinal();
                    break;
                case 2:
                    year = Integer.parseInt(nextToken);
                    break;
                default:
                    break;
                }
                index++;
            }
            if ((dayOfMonth < 0) || (month < 0) || (year < 0)) {
                throw new UnsupportedOperationException("Unable to parse date");
            }
            cal = new GregorianCalendar(year, month, dayOfMonth);
            DATE_CACHE.put(dateStr, cal);
        }
        return cal.getTime();
    }

}
