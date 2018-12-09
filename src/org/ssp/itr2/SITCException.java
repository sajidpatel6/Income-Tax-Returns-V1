package org.ssp.itr2;

public class SITCException extends Exception {

    private static final long serialVersionUID = -8955040385795249512L;

    public SITCException(final Exception exception) {
        super(exception);
    }

    SITCException(final String errorString) {
        super(errorString);
    }
}
