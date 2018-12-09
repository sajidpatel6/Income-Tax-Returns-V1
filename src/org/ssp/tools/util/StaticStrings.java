package org.ssp.tools.util;

public interface StaticStrings {
    String ACCESS_PASSWORD = "";

    String ACCESS_URL = "jdbc:odbc:NSTDB";

    String ACCESS_USER = "";

    String DATE_DDMMMYYYY = "dd-MMM-yyyy";

    String DATE_YYYYMMDD = "yyyyMMdd";

    String DIR_BHAV = "Data/bhav";

    String DIR_TSDATA = "Data/WLFiles/";

    String FILE_EQ_TRANS = "Data/EquitiesTransactions.csv";

    String FILE_LOCAL_FNO_LOTS = "Data/fo_mktlots.csv";

    String FORMAT_CHLOV = "CHLOV";

    String FORMAT_OHLCV = "OHLCV";

    String MYSQL_DRIVER = "com.mysql.jdbc.Driver";

    String MYSQL_PASSWORD = "guest";

    String MYSQL_URL = "jdbc:mysql://127.0.0.1:3306/nstdb";

    String MYSQL_USER = "guest";

    String NEWDB_URL = "jdbc:mysql://127.0.0.1:3306/newdb";

    String ODBC_DRIVER = "sun.jdbc.odbc.JdbcOdbcDriver";

    String TSHEADER = "<TICKER>,<DTYYYYMMDD>,<OPEN>,<HIGH>,<LOW>,<CLOSE>,<VOL>";

    String FILE_NIFTY_VAL_CSV = "Data/WLFiles/Indices/NIFTY.csv";

    String FILE_NIFTY_PE_CSV = "Data/WLFiles/PEs/NIFTY_PE.csv";

    String FILE_VIX_CSV = "Data/WLFiles/vix/vix.csv";

    String FILE_CORP_ACTS_CSV = "Data/WLFiles/CorporateActions/corp_acts.csv";

    String LOCAL_FNO_LOTS_FILE = "fo_mktlots.csv";
}
