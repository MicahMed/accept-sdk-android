package net.authorize.acceptsdk.util;

/**
 * Enumeration describing available currencies:
 * <p>
 * Available currencies:
 * <ul>
 * <li>CAD ( $ )</li>
 * <li>EUR ( € )</li>
 * <li>GBP ( £ )</li>
 * <li>USD ( $ )</li>
 * </ul>
 */
public enum SDKCurrency {

  CAD("$"), EUR("€"), GBP("£"), USD("$");

  SDKCurrency(String symbol) {
    this.symbol = symbol;
  }

  private final String symbol;

  public String symbol() {
    return symbol;
  }

  public static String[] names() {
    SDKCurrency[] currency = values();
    String[] names = new String[currency.length];

    for (int i = 0; i < currency.length; i++) {
      names[i] = currency[i].name();
    }

    return names;
  }

  public static String[] symbols() {
    SDKCurrency[] currency = values();
    String[] symbols = new String[currency.length];

    for (int i = 0; i < currency.length; i++) {
      symbols[i] = currency[i].symbol();
    }

    return symbols;
  }
}
