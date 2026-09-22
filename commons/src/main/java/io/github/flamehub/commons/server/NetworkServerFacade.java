package io.github.flamehub.commons.server;

import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.redis.RedisService;
import io.github.flamehub.commons.redis.cache.RedisCache;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public final class NetworkServerFacade extends RedisCache<String, NetworkServer> {

  private final NetworkServerSettingsCache networkServerSettingsCache;

  private NetworkServer current;

  public NetworkServerFacade(
      final RedisMessenger redisMessenger,
      final RedisService redisService,
      final NetworkServerSettingsCache networkServerSettingsCache
  ) {
    super(redisMessenger, redisService, NetworkServer.class, "network-servers", false);
    this.networkServerSettingsCache = networkServerSettingsCache;
  }

  public NetworkServerSettings getSetting(final String serverName) {
    return networkServerSettingsCache.get(serverName);
  }

  public void putSetting(final String serverName, final NetworkServerSettings settings) {
    networkServerSettingsCache.put(serverName, settings);
  }

  public Set<String> allCategories() {
    final Set<String> set = new HashSet<>();
    for (final NetworkServer value : values()) {
      set.add(value.getCategory());
    }

    return set;
  }

  public int getGlobalPlayers() {
    int online = 0;
    for (final String allCategory : allCategories()) {
      if ("proxy".equals(allCategory)) {
        continue;
      }

      online += getPlayersFrom(allCategory);
    }

    return online;
  }

  public List<NetworkServer> findServersByCategory(final String category) {
    return values().stream()
        .filter(networkServer -> networkServer.getCategory().equals(category))
        .collect(Collectors.toList());
  }

  public List<String> findServerNamesByCategory(final String category) {
    return findServersByCategory(category).stream().map(NetworkServer::getName).toList();
  }

  public Optional<NetworkServer> findByName(final String name) {
    return Optional.ofNullable(get(name));
  }

  public int getPlayersFrom(final String category) {
    return findServersByCategory(category).stream()
        .mapToInt(networkServer -> networkServer.getStatistics().getPlayers()).sum();
  }

  public int getPlayersLimitFrom(final String category) {
    return findServersByCategory(category).stream()
        .map(networkServer -> networkServerSettingsCache.get(networkServer.getName()))
        .mapToInt(NetworkServerSettings::getPlayersLimit)
        .sum();
  }

  public NetworkServer getLeastCrowded(final String category) {
    final Set<NetworkServer> servers = new HashSet<>();
    for (final NetworkServer networkServer : findServersByCategory(category)) {
      if (networkServer == null) {
        continue;
      }

      final NetworkServerSettings settings = networkServerSettingsCache.get(networkServer.getName());
      if (networkServer.isOnline() && !settings.isFrozen()) {
        servers.add(networkServer);
      }
    }

    return servers.stream()
        .min(Comparator.comparingInt(value -> value.getStatistics().getPlayers()))
        .orElse(getRandom(category));
  }

  public NetworkServer getRandom(final String category) {
    final List<NetworkServer> servers = values().stream()
        .filter(networkServer -> findServersByCategory(category).contains(networkServer))
        .filter(networkServer -> {
          final NetworkServerSettings settings = networkServerSettingsCache.get(networkServer.getName());
          return settings.isFrozen();
        })
        .toList();

    if (servers.isEmpty()) {
      return null;
    }

    return servers.get(ThreadLocalRandom.current().nextInt(0, servers.size()));
  }

  public Set<NetworkServer> values(final String category) {
    return values().stream()
        .filter(networkServer -> findServersByCategory(category).contains(networkServer))
        .collect(Collectors.toSet());
  }

  public TreeSet<NetworkServer> sortedValues(final Collection<NetworkServer> values) {
    final TreeSet<NetworkServer> servers = new TreeSet<>(
        Comparator.comparing(NetworkServer::getName));
    servers.addAll(values);

    return servers;
  }

  public NetworkServer getCurrent() {
    return current;
  }

  public void setCurrent(final NetworkServer current) {
    this.current = current;
  }


}