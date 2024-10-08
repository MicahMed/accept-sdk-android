package net.authorize.acceptsdk;

import java.util.Calendar;

/**
 * Class contains utility methods to validate card details.
 * <p>
 * Created by kbollepa on 7/7/2016.
 */
public class ValidationManager {

    /* ----------------- Validations related to Card data -------------------------*/

    /**
     * Method validates given card number is valid w.r.t Luhn formula
     *
     * @param cardNumber String
     * @return true if card is valid and false for invalid card.
     */
    public static boolean isValidCardNumber(String cardNumber) {
        if (cardNumber == null) {
            return false;
        }
        cardNumber = cardNumber.trim();
        if (cardNumber.length() < 6) {
            return false;
        }
        if (!cardNumber.matches("\\d+")) {
            return false;
        }

        // reversing the string
        String reversedNumber = new StringBuilder(cardNumber).reverse().toString();

        String mutilatedString = "";

        // Even digits are doubled, odd digits are not modified
        for (int i = 0; i < reversedNumber.length(); i++) {
            int c = Integer.parseInt(String.valueOf(reversedNumber.charAt(i)));
            if (i % 2 != 0) {
                c *= 2;
            }
            mutilatedString += c;
        }

        int sumOfDigits = 0;

        // summing up all digits
        for (int i = 0; i < mutilatedString.length(); i++) {
            int c = Integer.parseInt(String.valueOf(mutilatedString.charAt(i)));
            sumOfDigits += c;
        }

        return sumOfDigits > 0 && sumOfDigits % 10 == 0;
    }

    /**
     * Method validates expiration month of card
     *
     * @param month a string in two-digit format
     * @return true if expiration month is valid and false if invalid.
     */
    public static boolean isValidExpirationMonth(String month) {

        if (month == null || month.length() != 2 || (!month.matches("\\d+"))) {
            return false;
        }

        int checkMonth = Integer.parseInt(month);
        return checkMonth >= 1 && checkMonth <= 12;
    }

    /**
     * Method validates expiration month of card
     *
     * @param year a string in 4-digit format
     * @return true if expiration month is valid and false if invalid.
     */
    public static boolean isValidExpirationYear(String year) {
        if (year == null || (year.length() != 4)) {
            return false;
        }
        return year.matches("\\d+");
    }

    /**
     * Validation for Past date
     *
     * @param month two digit string
     * @param year four digit string
     * @return true if validation is success, false if fails.
     */

    public static boolean isValidExpirationDate(String month, String year) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int currentMonth =
                Calendar.getInstance().get(Calendar.MONTH) + 1; // since JANUARY = 0 for Calendar class
        int checkYear = Integer.parseInt(year);
        int checkMonth = Integer.parseInt(month);

        if (checkYear < currentYear) {
            return false;
        }
        return (checkYear != currentYear) || (checkMonth >= currentMonth);
    }

    /**
     * Method validates cvv code of card.
     * CVV code should be numeric string and length should be 3 or 4.
     *
     * @param cvvCode a string
     * @return true if validation is success, false if fails.
     */
    public static boolean isValidCVV(String cvvCode) {
        if (cvvCode == null || (cvvCode.length() < 3 || cvvCode.length() > 4)) {
            return false;
        }

        return cvvCode.matches("\\d+");
    }

    /**
     * Method validates zip code of card.
     * zip code length should be between 1 &amp; 20.
     *
     * @param zipCode a string
     * @return true if validation is success, false if fails.
     */
    public static boolean isValidZipCode(String zipCode) {
        return zipCode != null && (!zipCode.isEmpty() && zipCode.length() <= 20);
    }

    /**
     * Method validates card holder name.
     * Card holder name length should be between 1 &amp; 64.
     *
     * @param fullName a string
     * @return true if validation is success, false if fails.
     */

    public static boolean isValidCardHolderName(String fullName) {
        return fullName != null && (!fullName.isEmpty() && fullName.length() <= 64);
    }

    /* ----------------- Validations related to Finger Print -------------------------*/

    /**
     * Method validates amount.
     * Amount should be positive numbers with 1-2 digit fraction.
     *
     * @param amount as double
     * @return true if validation is success, false if fails.
     */
    public static boolean isValidAmount(double amount) {
        return !(amount <= 0);
    }

    /* ----------------- Validations related to Finger Print -------------------------*/

    /**
     * Method validates Time stamp.
     * Amount should be positive numbers.
     *
     * @param timestamp as long
     * @return true if validation is success, false if fails.
     */
    public static boolean isValidTimeStamp(long timestamp) {
        return timestamp >= 0;
    }

    /* ----------------- Common Validations-------------------------*/
    public static boolean isValidString(String string) {
        return string != null && (!string.isEmpty());
    }
}