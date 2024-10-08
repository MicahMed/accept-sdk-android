package net.authorize.acceptsdk.util;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;
import java.util.TimeZone;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

/**
 * This class provides static common methods to be used by other classes in the
 * project.
 */

public class SDKUtils {

    /** Format of 'date': yyyy-MM-dd */
    private final static String DATE_FORMAT = "yyyy-MM-dd";

    /** Format of 'time': HH:mm AM/PM */
    private final static String TIME_FORMAT = "hh:mm a";

    private final static String DEFAULT_TIMEZONE = "America/Los_Angeles";

    private final static String UTC_TIMEZONE = "UTC";

    private final static String BASIC_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    /** Max connection attempts = 5 */
    private final static int CONNECTION_ATTEMPTS = 5;

    /** Connection timeout = 10000ms */
    private final static int CONNECTION_TIMEOUT = 10000;

    /** Connection timeout = 30000ms */
    private final static int RECEIVE_DATA_TIMEOUT = 30000;

    /** Port: 80 */
    private final static int PORT_80 = 80;

    /** Port: 443 */
    private final static int PORT_443 = 443;

    /**
     * Converts given InputStream to String
     *
     * @param is InputStream
     * @return String from InputStream
     */
    public static String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is);
        s.useDelimiter("\\A");
        String result = s.hasNext() ? s.next() : "";
        s.close();
        return result;
    }

    public static String convertTimeInMillisToDate(String timeStamp, String timezone) {
        return getTime(timeStamp, timezone, DATE_FORMAT);
    }

    public static String convertTimeInMillisToTime(String timeStamp, String timezone) {
        return getTime(timeStamp, timezone, TIME_FORMAT);
    }

    private static String getTime(String timeStamp, String timezone, String format) {
        TimeZone utc = TimeZone.getTimeZone(UTC_TIMEZONE);
        long time = stringToLong(timeStamp);
        TimeZone timeZone;
        if (!TextUtils.isEmpty(timezone)) {
            timeZone = TimeZone.getTimeZone(timezone);
        } else {
            timeZone = TimeZone.getTimeZone(DEFAULT_TIMEZONE);
        }
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf =
                new SimpleDateFormat(BASIC_DATE_FORMAT);
        sdf.setTimeZone(timeZone);
        Date date = new Date(time);
        String input = sdf.format(date); // here we have UTC time
        GregorianCalendar cal = new GregorianCalendar(utc);
        // now we try to parse it to local time
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat s =
                    new SimpleDateFormat(BASIC_DATE_FORMAT);
            s.setTimeZone(utc);
            cal.setTime(s.parse(input));
            Date date2 = new Date(cal.getTime().getTime());
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDate = new SimpleDateFormat(format);
            return simpleDate.format(date2);
        } catch (ParseException e) {
            // return UTC time if parse failed
            return input;
        }
    }

    public static String convertToLocalTime(String input) {
        return getTime(input, TIME_FORMAT);
    }

    public static String convertToLocalDate(String input) {
        return getTime(input, DATE_FORMAT);
    }

    private static String getTime(String input, String format) {
        TimeZone utc = TimeZone.getTimeZone(UTC_TIMEZONE);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat f = new SimpleDateFormat(BASIC_DATE_FORMAT);
        f.setTimeZone(utc);
        GregorianCalendar cal = new GregorianCalendar(utc);
        try {
            cal.setTime(f.parse(input));
            Date date = new Date(cal.getTime().getTime());
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDate = new SimpleDateFormat(format);
            return simpleDate.format(date);
        } catch (ParseException e) {
            return input;
        }
    }

    public static long stringToLong(String value) {
        long result = 0;
        if (value != null) {
            try {
                result = Long.parseLong(value);
            } catch (NumberFormatException exception) {
                exception.printStackTrace();
            }
        }
        return result;
    }

    public static HttpsURLConnection getHttpsURLConnection(String urlString, String requestMethod,
                                                           boolean doOutput) throws IOException {
        URL url = new URL(urlString);
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        SSLSocketFactory socketFactory = getSSLSocketFactory();
        if (socketFactory != null) {
            urlConnection.setSSLSocketFactory(getSSLSocketFactory());
        }

        if (requestMethod != null) {
            urlConnection.setRequestMethod(requestMethod);
        }
        urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
        urlConnection.setReadTimeout(RECEIVE_DATA_TIMEOUT);
        urlConnection.setDoOutput(doOutput);
        urlConnection.setDoInput(true);
        return urlConnection;
    }


    public static SSLSocketFactory getSSLSocketFactory() {
        SSLContext context;
        try {
            context = SSLContext.getInstance("TLSv1.2");
            context.init(null, null, new java.security.SecureRandom());
            return context.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            return null;
        }
    }

    public static SDKCurrency getSDKCurrencyFromString(String currencyString) {
        return switch (currencyString) {
            case "USD" -> SDKCurrency.USD;
            case "CAD" -> SDKCurrency.CAD;
            case "EUR" -> SDKCurrency.EUR;
            case "GBP" -> SDKCurrency.GBP;
            default -> null;
        };
    }

    /**
     * Convert BigDecimal amount value to String
     *
     * @param value the amount in Big Decimal to be converted
     * @return String, Amount in string format.
     */
    public static String getAmountStringFromBigDecimal(BigDecimal value) {
        BigDecimal amount = value.setScale(2, RoundingMode.CEILING);
        return amount.toPlainString();
    }
}