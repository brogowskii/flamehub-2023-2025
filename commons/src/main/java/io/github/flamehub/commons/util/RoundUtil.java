package io.github.flamehub.commons.util;

public final class RoundUtil {

    private RoundUtil() {
    }

    public static double round(double value, int decimals) {
        double p = Math.pow(10.0, decimals);
        return Math.round(value * p) / p;
    }

}