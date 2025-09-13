package io.github.flamehub.commons.bukkit.tab;

import java.util.HashMap;
import java.util.Map;

public final class TablistPage {

  private Map<Integer, String> lines = new HashMap<>() {{

    put(0, "&6&lINFORMACJE O TOBIE");
    put(1, "&7Nick: &e%player%");
    put(2, "&7Ranga: &f%rank%");
    put(3, " ");
    put(4, "&7Zabojstwa: &c%kills%");
    put(5, "&7Smierci: &c%deaths%");
    put(6, "&7Konto: &a$%money%");
    put(7, " ");
    put(8, "&6&lINFORMACJE O KLANIE");
    put(9, "&7Klan: &b%clan%");
    put(10, "&7Lider: &b%clan_leader%");
    put(11, "&7Online: &a%clan_online%");

    put(20, "&a&l$ TOPKA KASY $");
    put(21, "&71. &a%cash_1%");
    put(22, "&72. &a%cash_2%");
    put(23, "&73. &a%cash_3%");
    put(24, "&74. &a%cash_4%");
    put(25, "&75. &a%cash_5%");
    put(26, "&76. &a%cash_6%");
    put(27, "&77. &a%cash_7%");
    put(28, "&78. &a%cash_8%");
    put(29, "&79. &a%cash_9%");
    put(30, "&710. &a%cash_10%");
    put(40, "&6&lTOPKA CZASU");
    put(41, "&71. &e%time_1%");
    put(42, "&72. &e%time_2%");
    put(43, "&73. &e%time_3%");
    put(44, "&74. &e%time_4%");
    put(45, "&75. &e%time_5%");
    put(46, "&76. &e%time_6%");
    put(47, "&77. &e%time_7%");
    put(48, "&78. &e%time_8%");
    put(49, "&79. &e%time_9%");
    put(50, "&710. &e%time_10%");
    put(60, "&d&lNAJBLIZSZE WYDARZENIA");
    put(61, "&7Wykopaliska: &e%event_wyk%min");
    put(62, "&7Krolowa Pajakow: &e%event_spider%min");
    put(63, "&7Metin: &e%event_metin%min");
    put(64, "&7Fenrir: &e%event_fenrir%min");
    put(66, "&a&lPRZYDATNE KOMENDY");
    put(67, "&7/klan &8- &7Info o klanach");
    put(68, "&7/ustawienia &8- &7Ustawienia");
    put(69, "&7/warp &8- &7Lista warpow");
    put(70, "&7/eventy &8- &7Opis eventow");
    put(71, "&7/kit &8- &7Lista kitow");
    put(72, "&7/rangi &8- &7Opis rang");

  }};

  public Map<Integer, String> getLines() {
    return lines;
  }

}
