package io.github.flamehub.commons.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

//source https://github.com/FunnyGuilds/FunnyGuilds/blob/master/plugin/src/main/java/net/dzikoysk/funnyguilds/shared/TimeUtils.java
public final class TimeUtil {

  private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");

  private static final TimeDivision[] TIME_DIVISIONS = new TimeDivision[]{
      TimeDivision.YEAR,
      TimeDivision.MONTH,
      TimeDivision.DAY,
      TimeDivision.HOUR,
      TimeDivision.MINUTE,
      TimeDivision.SECOND,
  };

  private TimeUtil() {
  }

  public static Date dateFromString(String date) throws ParseException {
    return DATE_FORMAT.parse(date);
  }

  public static String formatDate(Instant instant) {
    Date date = Date.from(instant);
    return DATE_FORMAT.format(date);
  }

  public static String formatDate(Date date) {
    return DATE_FORMAT.format(date);
  }

  public static String formatTimeSimple(Duration duration) {
    return TimeUtil.formatTimeSimple(duration.toMillis(), true);
  }

  public static String formatTimeSimple(long millis) {
    return formatTimeSimple(millis, true);
  }

  public static String formatTimeSimple(long millis, boolean appendSeconds) {
    long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
    if (seconds <= 0) {
      return (int) millis + "ms";
    }

    long minutes = seconds / 60;
    seconds = seconds % 60;
    long hours = minutes / 60;
    minutes = minutes % 60;
    long day = hours / 24;
    hours = hours % 24;
    long years = day / 365;
    day = day % 365;

    StringBuilder time = new StringBuilder();

    if (years != 0) {
      time.append(years).append("r ");
    }

    if (day != 0) {
      time.append(day).append("d ");
    }

    if (hours != 0) {
      time.append(hours).append("h ");
    }

    if (minutes != 0) {
      time.append(minutes).append("m ");
    }

    if (seconds != 0 && appendSeconds) {
      time.append(seconds).append("s");
    }

    return time.toString().trim();

  }

  public static String formatTime(Duration duration) {
    return TimeUtil.formatTime(duration, " ");
  }

  public static String formatTime(Duration duration, String delimiter) {
    return TimeUtil.formatTime(duration, delimiter, TIME_DIVISIONS);
  }

  public static String formatTime(Duration time, String delimiter,
      TimeDivision[] durationDivisions) {
    long millis = time.toMillis();
    LinkedHashMap<TimeDivision, Long> timeParts = new LinkedHashMap<>();

    for (final TimeDivision division : durationDivisions) {
      long divisionTime = millis / division.getMillis();

      millis -= divisionTime * division.getMillis();
      timeParts.put(division, divisionTime);
    }

    return parseTimeParts(timeParts, delimiter);
  }

  private static String parseTimeParts(LinkedHashMap<TimeDivision, Long> timeParts,
      String delimiter) {
    StringBuilder timeStringBuilder = new StringBuilder();
    for (Map.Entry<TimeDivision, Long> timePart : timeParts.entrySet()) {
      long partValue = timePart.getValue();
      if (partValue == 0) {
        continue;
      }

      timeStringBuilder.append(delimiter).append(timePart.getKey().getFormatted(partValue));
    }

    if (timeStringBuilder.length() == 0) {
      return TimeDivision.SECOND.getFormatted(0);
    }

    return timeStringBuilder.substring(delimiter.length());
  }

  public static Duration parseTime(String time) {
    final StringBuilder tempNumber = new StringBuilder();
    long resultTime = 0L;

    final char[] stringChars = time.toLowerCase().toCharArray();
    timeLoop:
    for (int i = 0; i < stringChars.length; i++) {
      final char c = stringChars[i];
      if (c >= '0' && c <= '9') {
        tempNumber.append(c);
        continue;
      }

      for (final TimeDivision timeDivision : TimeDivision.values()) {
        abbreviationLoop:
        for (final String abbreviation : timeDivision.getAbbreviations()) {
          if (i + abbreviation.length() > stringChars.length) {
            continue;
          }

          final char[] abbreviationChars = abbreviation.toCharArray();
          for (int a = 0; a < abbreviationChars.length; a++) {
            if (abbreviationChars[a] != stringChars[i + a]) {
              continue abbreviationLoop;
            }
          }

          char next = '0';
          if (i + abbreviation.length() < stringChars.length) {
            next = stringChars[i + abbreviation.length()];
          }

          if (next < '0' || next > '9') {
            continue;
          }

          if (tempNumber.length() == 0) {
            return Duration.ofSeconds(0);
          }

          resultTime += Long.parseLong(tempNumber.toString()) * timeDivision.getMillis();
          tempNumber.setLength(0);

          i += abbreviation.length() - 1;
          continue timeLoop;
        }
      }

      return Duration.ofSeconds(0);
    }

    if (tempNumber.length() != 0) {
      return Duration.ofSeconds(0);
    }

    return Duration.ofMillis(resultTime);
  }

  public enum TimeDivision {

    SECOND(1000L, "sekunda", "sekundy", "sekund", "s", "sek"),
    MINUTE(60000L, "minuta", "minuty", "minut", "m", "min"),
    HOUR(3600000L, "godzina", "godziny", "godzin", "h", "godz"),
    DAY(86400000L, "dzien", "dni", "dni", "d", "dni", "day"),
    WEEK(604800000L, "tydzien", "tygodnie", "tygodni", "w", "t", "tyg"),
    MONTH(2592000000L, "miesiac", "miesiace", "miesiecy", "mo", "ms", "mc", "mies"),
    YEAR(31536000000L, "rok", "lata", "lata", "y", "r", "l", "lat", "rok");

    private final long millis;

    private final String singularForm;
    private final String doubleForm;
    private final String pluralForm;

    private final String[] abbreviations;

    TimeDivision(long millis, String singularForm, String doubleForm, String pluralForm,
        String... abbreviations) {
      this.millis = millis;
      this.singularForm = singularForm;
      this.doubleForm = doubleForm;
      this.pluralForm = pluralForm;
      this.abbreviations = abbreviations;
    }

    public long getMillis() {
      return millis;
    }

    public String getForm(long amount) {
      if (amount == 1) {
        return singularForm;
      }

      long onesNumber = amount % 10;
      long tensNumber = amount % 100;

      if (onesNumber < 2 || onesNumber > 4) {
        return pluralForm;
      }

      if (tensNumber >= 12 && tensNumber <= 14) {
        return pluralForm;
      }

      return doubleForm;
    }

    public String getFormatted(long amount) {
      return amount + " " + getForm(amount);
    }

    public String[] getAbbreviations() {
      return Arrays.copyOf(abbreviations, abbreviations.length);
    }

  }

}