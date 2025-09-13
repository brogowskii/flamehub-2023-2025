package io.github.flamehub.commons.util;

public final class RoundUtil {

  private RoundUtil() {
  }

  public static double round(final double value, final int decimals) {
    final double p = Math.pow(10.0, decimals);
    return Math.round(value * p) / p;
  }

}