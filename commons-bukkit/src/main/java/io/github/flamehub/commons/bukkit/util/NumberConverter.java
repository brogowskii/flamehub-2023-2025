package io.github.flamehub.commons.bukkit.util;

public final class NumberConverter {

    private NumberConverter() {

    }

    public static String convertNumber(double number) {
        if (number < 1000) {
            return String.format("%.2f", number);
        } else if (number < 1000000) {
            return String.format("%.2fk", number / 1000);
        } else if (number < 1000000000) {
            return String.format("%.2fM", number / 1000000);
        } else if (number < 1000000000000L) {
            return String.format("%.2fMLD", number / 1000000000);
        } else if (number < 1000000000000000L) {
            return String.format("%.2fB", number / 1000000000000L);
        } else {
            return String.format("%.2fT", number / 1000000000000000L);
        }
    }

}
