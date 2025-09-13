package io.github.flamehub.commons.bukkit.tab;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import com.github.retrooper.packetevents.protocol.player.TextureProperty;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoRemove;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class PageableTablistService implements TablistService {

  private static final int SLOTS = 80;
  private static final int DEFAULT_LATENCY = 0;

  /**
   * Minimalny odstęp między kolejnymi update’ami dla danego gracza (ms).
   */
  private static final long MIN_UPDATE_INTERVAL_MS = 200L;

  private static final TablistPage EMPTY_PAGE = new TablistPage();

  private final Plugin plugin;
  private final TablistConfig config;

  /**
   * Per-player stan tablisty.
   */
  private final Map<UUID, TabState> states = new ConcurrentHashMap<>();

  public PageableTablistService(final Plugin plugin,
      final TablistConfig config,
      final boolean ignoredUpdateLatencyFlag) {
    this.plugin = plugin;
    this.config = config;
  }

  /**
   * Wołaj cyklicznie z taska (np. co 10–15 ticków): pełny scan 80 slotów + diff.
   */
  @Override
  public void send(final Player player) {
    fullUpdate(player);
  }

  /**
   * Pierwsza inicjalizacja – wołaj w PlayerJoinEvent z lekkim opóźnieniem (2–5 ticków).
   */
  public void firstInit(final Player player) {
    if (!ensureMainThread(() -> firstInit(player))) {
      return;
    }
    if (player == null || !player.isOnline()) {
      return;
    }

    final UUID id = player.getUniqueId();
    TabState state = states.get(id);
    if (state != null && state.initialized) {
      return;
    }

    if (state == null) {
      state = new TabState(buildProfiles(id));
      states.put(id, state);
    }

    final List<WrapperPlayServerPlayerInfoUpdate.PlayerInfo> addEntries = new ArrayList<>(SLOTS);
    for (int slot = 0; slot < SLOTS; slot++) {
      final UserProfile profile = state.profiles.get(slot);
      addEntries.add(new WrapperPlayServerPlayerInfoUpdate.PlayerInfo(
          profile,
          true,
          DEFAULT_LATENCY,
          GameMode.SURVIVAL,
          Component.text(" "),
          null
      ));
    }

    final EnumSet<WrapperPlayServerPlayerInfoUpdate.Action> addActions = EnumSet.of(
        WrapperPlayServerPlayerInfoUpdate.Action.ADD_PLAYER,
        WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_LISTED,
        WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_GAME_MODE,
        WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_DISPLAY_NAME
    );

    try {
      final WrapperPlayServerPlayerInfoUpdate addPacket =
          new WrapperPlayServerPlayerInfoUpdate(addActions, addEntries);
      PacketEvents.getAPI().getPlayerManager().sendPacket(player, addPacket);
      state.initialized = true;
      state.lastUpdateMs = 0L;
    } catch (final Throwable t) {
      states.remove(id);
      plugin.getLogger().warning("[Tablist] Init failed for " + safeName(player) + ": " + t);
    }
  }

  /**
   * Remove wszystkich 80 wpisów + cleanup – wołaj w PlayerQuitEvent.
   */
  public void clearAndRemoveEntries(final Player player) {
    if (!ensureMainThread(() -> clearAndRemoveEntries(player))) {
      return;
    }
    if (player == null) {
      return;
    }

    final UUID id = player.getUniqueId();
    final TabState state = states.remove(id);
    if (state == null || state.profiles.isEmpty()) {
      return;
    }

    final List<UUID> uuids = new ArrayList<>(SLOTS);
    for (final UserProfile p : state.profiles) {
      uuids.add(p.getUUID());
    }

    try {
      final WrapperPlayServerPlayerInfoRemove removePacket =
          new WrapperPlayServerPlayerInfoRemove(uuids);
      PacketEvents.getAPI().getPlayerManager().sendPacket(player, removePacket);
    } catch (final Throwable t) {
      plugin.getLogger().warning("[Tablist] Remove failed for " + safeName(player) + ": " + t);
    }
  }

  /**
   * Czyści wszystkich online – wołaj przy reloadzie plugina.
   */
  public void clearAllOnline() {
    if (!Bukkit.isPrimaryThread()) {
      Bukkit.getScheduler().runTask(plugin, this::clearAllOnline);
      return;
    }
    for (final Player p : Bukkit.getOnlinePlayers()) {
      try {
        clearAndRemoveEntries(p);
      } catch (final Throwable ignored) {
      }
    }
    states.clear();
  }

  // ================== Pełny scan 80 slotów + diff w jednej paczce ==================

  private void fullUpdate(final Player player) {
    if (!ensureMainThread(() -> fullUpdate(player))) {
      return;
    }
    if (player == null || !player.isOnline()) {
      return;
    }

    final UUID pid = player.getUniqueId();
    final TabState state = states.get(pid);
    if (state == null || !state.initialized || state.profiles.size() != SLOTS) {
      return;
    }

    final long now = System.currentTimeMillis();
    if (now - state.lastUpdateMs < MIN_UPDATE_INTERVAL_MS) {
      return;
    }
    state.lastUpdateMs = now;

    final TablistPage page = config.getPages().getOrDefault(1, EMPTY_PAGE);

    final List<WrapperPlayServerPlayerInfoUpdate.PlayerInfo> changed = new ArrayList<>();

    for (int slot = 0; slot < SLOTS; slot++) {
      final UserProfile profile = state.profiles.get(slot);

      final String template = page.getLines().getOrDefault(slot, " ");
      String raw;
      try {
        raw = PlaceholderAPI.setPlaceholders(player, template);
      } catch (final Throwable t) {
        raw = " ";
      }
      if (raw == null || raw.isEmpty()) {
        raw = " ";
      }

      // diff – wyślij tylko jeśli treść się zmieniła
      if (!raw.equals(state.lastRaw[slot])) {
        state.lastRaw[slot] = raw;
        final Component display = TextUtil.parse(raw);

        changed.add(new WrapperPlayServerPlayerInfoUpdate.PlayerInfo(
            profile,
            true,
            DEFAULT_LATENCY,
            GameMode.SURVIVAL,
            display,
            null
        ));
      }
    }

    if (!changed.isEmpty()) {
      final EnumSet<WrapperPlayServerPlayerInfoUpdate.Action> actions =
          EnumSet.of(WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_DISPLAY_NAME);

      try {
        final WrapperPlayServerPlayerInfoUpdate packet =
            new WrapperPlayServerPlayerInfoUpdate(actions, changed);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
      } catch (final Throwable t) {
        plugin.getLogger().warning("[Tablist] Update failed for " + safeName(player) + ": " + t);
      }
    }
  }

  // ================== Helpery ==================

  private List<UserProfile> buildProfiles(final UUID playerId) {
    final List<UserProfile> profiles = new ArrayList<>(SLOTS);

    final String headValue = safe(config.getHeadValue());
    final String headSignature = safe(config.getHeadSignature());
    final boolean hasSkin = !headValue.isEmpty() && !headSignature.isEmpty();

    for (int slot = 0; slot < SLOTS; slot++) {
      final UUID uuid = UUID.nameUUIDFromBytes(("tab-" + playerId + "-" + slot).getBytes());
      final String profileName = uniqueProfileName(slot);
      final UserProfile profile = new UserProfile(uuid, profileName);
      if (hasSkin) {
        profile.getTextureProperties()
            .add(new TextureProperty("textures", headValue, headSignature));
      }
      profiles.add(profile);
    }
    return profiles;
  }

  /**
   * Stabilna, ≤16 znaków: FLAME-TAB-XX
   */
  private String uniqueProfileName(final int slot) {
    final String base = "FLAME-TAB-" + String.format("%02d", slot);
    return base.length() > 16 ? base.substring(0, 16) : base;
  }

  private boolean ensureMainThread(final Runnable rerunOnMain) {
    if (!Bukkit.isPrimaryThread()) {
      Bukkit.getScheduler().runTask(plugin, rerunOnMain);
      return false;
    }
    return true;
  }

  private static String safe(final String s) {
    return s == null ? "" : s;
  }

  private static String safeName(final Player p) {
    try {
      return p.getName();
    } catch (final Throwable t) {
      return "<unknown>";
    }
  }

  // ================== Stan per gracz ==================

  private static final class TabState {

    final List<UserProfile> profiles;           // stały zestaw profili dla 80 slotów
    final String[] lastRaw = new String[SLOTS]; // cache ostatnich stringów do diffu
    volatile boolean initialized;
    volatile long lastUpdateMs;

    TabState(final List<UserProfile> profiles) {
      this.profiles = profiles;
      Arrays.fill(lastRaw, " ");
    }
  }
}
