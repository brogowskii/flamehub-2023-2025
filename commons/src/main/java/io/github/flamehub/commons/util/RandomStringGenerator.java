package io.github.flamehub.commons.util;

import java.util.Random;

public final class RandomStringGenerator {

    private RandomStringGenerator() {

    }

    private static final String ALLOWED_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static String generateStringWFromRandomCharacters(int length) {
        StringBuilder captcha = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(ALLOWED_CHARACTERS.length());
            captcha.append(ALLOWED_CHARACTERS.charAt(index));
        }

        return captcha.toString();
    }

}
