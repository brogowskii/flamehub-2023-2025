package io.github.flamehub.commons.bukkit.util;

public final class HexUtil {

    private HexUtil() {
    }

    public static String interpolateColors(String message, String hex1, String hex2) {
        int[] rgb1 = hexToRGB(hex1);
        int[] rgb2 = hexToRGB(hex2);

        StringBuilder result = new StringBuilder();
        int stepCount = message.length();

        for (int i = 0; i < stepCount; i++) {
            float fraction = (float) i / (stepCount - 1);
            int r = (int) (rgb1[0] + fraction * (rgb2[0] - rgb1[0]));
            int g = (int) (rgb1[1] + fraction * (rgb2[1] - rgb1[1]));
            int b = (int) (rgb1[2] + fraction * (rgb2[2] - rgb1[2]));

            result.append("&").append(rgbToHex(r, g, b)).append(message.charAt(i));
        }

        return result.toString();
    }

    private static int[] hexToRGB(String hex) {
        return new int[]{
                Integer.valueOf(hex.substring(1, 3), 16),
                Integer.valueOf(hex.substring(3, 5), 16),
                Integer.valueOf(hex.substring(5, 7), 16)
        };
    }

    private static String rgbToHex(int r, int g, int b) {
        return String.format("#%02X%02X%02X", r, g, b);
    }


}
