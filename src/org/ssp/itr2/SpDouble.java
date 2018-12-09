package org.ssp.itr2;

public class SpDouble {
    private final static int ACCURACY = 10000;
    transient private int exponent = 0;
    transient private int mantissa = 0;

    public SpDouble() {
        exponent = 0;
        mantissa = 0;
    }

    public SpDouble(final SpDouble oValue) {
        exponent = oValue.exponent;
        mantissa = oValue.mantissa;
    }

    public SpDouble(final String string) {

        final int iDecimalPos = string.indexOf('.');

        if (iDecimalPos == -1) {
            exponent = Integer.parseInt(string);
        } else {
            exponent = Integer.parseInt(string.substring(0, iDecimalPos));
            String sSubString = string.substring(iDecimalPos + 1);
            if (sSubString.length() < 4) {
                for (int iIndex = sSubString.length(); iIndex < 4; iIndex++) {
                    sSubString = sSubString.concat("0");
                }
            }
            mantissa = Integer.parseInt(sSubString);
        }
    }

    public void add(final int iValue) {
        exponent += iValue;
    }

    public void add(final SpDouble oValue) {
        mantissa += oValue.mantissa;

        if (mantissa >= ACCURACY) {
            mantissa -= ACCURACY;
            exponent += oValue.exponent + 1;
        } else {
            exponent += oValue.exponent;
        }
    }

    public boolean isEqualTo(final SpDouble oValue) {
        boolean bReturn = false;

        if ((oValue.exponent == exponent) && (oValue.mantissa == mantissa)) {
            bReturn = true;
        }

        return bReturn;
    }

    public boolean isGreatorThan(final SpDouble oValue) {
        boolean bReturn = false;

        if (oValue.exponent < exponent) {
            bReturn = true;
        } else if ((oValue.exponent == exponent) && (oValue.mantissa < mantissa)) {
            bReturn = true;
        }

        return bReturn;
    }

    public void set(final int iValue) {
        exponent = iValue;
        mantissa = 0;
    }

    public void set(final SpDouble oValue) {
        exponent = oValue.exponent;
        mantissa = oValue.mantissa;
    }

    public void subtract(final int iValue) {
        exponent -= iValue;
    }

    public void subtract(final SpDouble oValue) {
        mantissa -= oValue.mantissa;

        if (mantissa < 0) {
            mantissa += ACCURACY;
            exponent -= (oValue.exponent + 1);
        } else {
            exponent -= oValue.exponent;
        }
    }

    @Override
    public String toString() {
        String sReturn;
        if (mantissa == 0) {
            sReturn = Integer.toString(exponent);
        } else {
            sReturn = (exponent + "." + String.format("%04d", mantissa));
            while (sReturn.lastIndexOf('0') == (sReturn.length() - 1)) {
                sReturn = sReturn.substring(0, sReturn.length() - 1);
            }
            if (sReturn.lastIndexOf('.') == (sReturn.length() - 1)) {
                sReturn = sReturn.substring(0, sReturn.length() - 1);
            }
        }
        return sReturn;
    }

    public double value() {
        double dValue = 0.0;

        dValue = (exponent * ACCURACY) + mantissa;

        return dValue / ACCURACY;
    }
}
