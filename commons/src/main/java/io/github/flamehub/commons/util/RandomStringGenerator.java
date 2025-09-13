package io.github.flamehub.commons.util;

import java.util.Random;

public final class RandomStringGenerator {

  private static final String ALLOWED_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

  private RandomStringGenerator() {

  }

  public static String generateStringWFromRandomCharacters(final int length) {
    final StringBuilder captcha = new StringBuilder();
    final Random random = new Random();

    for (int i = 0; i < length; i++) {
      final int index = random.nextInt(ALLOWED_CHARACTERS.length());
      captcha.append(ALLOWED_CHARACTERS.charAt(index));
    }

    return captcha.toString();
  }

}
