package io.github.flamehub.commons.server;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public final class NetworkServerCache {

  private final Map<String, NetworkServer> networkServerMap = new ConcurrentHashMap<>();
  private NetworkServer current;

  public void add(NetworkServer server) {
    this.networkServerMap.put(server.getName(), server);
  }

  public Set<String> allCategories() {
    Set<String> set = new HashSet<>();
    for (NetworkServer value : this.networkServerMap.values()) {
      set.add(value.getCategory());
    }

    return set;
  }

  public int getGlobalPlayers() {
    int online = 0;
    for (String allCategory : allCategories()) {
      if (allCategory.equals("proxy")) {
        continue;
      }

      online += getPlayersFrom(allCategory);
    }

    return online;
  }

  public List<NetworkServer> findServersByCategory(String category) {
    return this.networkServerMap.values()
        .stream()
        .filter(networkServer -> networkServer.getCategory().equals(category))
        .collect(Collectors.toList());
  }

  public List<String> findServerNamesByCategory(String category) {
    return this.findServersByCategory(category)
        .stream()
        .map(NetworkServer::getName)
        .toList();
  }

  public Optional<NetworkServer> findByName(String name) {
    return Optional.ofNullable(this.networkServerMap.get(name));
  }

  public long getPlayersFrom(String category) {
    return this.findServersByCategory(category)
        .stream()
        .mapToLong(networkServer -> networkServer.getStatistics().getPlayers())
        .sum();
  }

  public long getPlayersLimitFrom(String category) {
    return this.findServersByCategory(category)
        .stream()
        .mapToLong(networkServer -> networkServer.getStatistics().getPlayersLimit())
        .sum();
  }

  public NetworkServer getLeastCrowded(String category) {
    Set<NetworkServer> servers = new HashSet<>();
    for (NetworkServer networkServer : this.findServersByCategory(category)) {
      if ((networkServer != null) && (networkServer.isOnline() && !networkServer.getStatistics()
          .isFrozen())) {
        servers.add(networkServer);
      }
    }

    return servers.stream()
        .min(Comparator.comparingInt(value -> value.getStatistics().getPlayers()))
        .orElse(getRandom(category));
  }

  public NetworkServer getRandom(String category) {
    List<NetworkServer> servers = this.values().stream()
        .filter(networkServer -> this.findServersByCategory(category).contains(networkServer))
        .filter(networkServer -> !networkServer.getStatistics().isFrozen())
        .toList();

    if (servers.isEmpty()) {
      return null;
    }

    return servers.get(ThreadLocalRandom.current().nextInt(0, servers.size()));
  }

  public Set<NetworkServer> values() {
    return new HashSet<>(networkServerMap.values());
  }

  public Set<NetworkServer> values(String category) {
    return this.values().stream()
        .filter(networkServer -> this.findServersByCategory(category).contains(networkServer))
        .collect(Collectors.toSet());
  }

  public TreeSet<NetworkServer> sortedValues(Collection<NetworkServer> values) {
    TreeSet<NetworkServer> servers = new TreeSet<>(Comparator.comparing(NetworkServer::getName));
    servers.addAll(values);

    return servers;
  }

  public NetworkServer getCurrent() {
    return current;
  }

  public void setCurrent(NetworkServer current) {
    this.current = current;
  }

  public void clear() {
    this.networkServerMap.clear();
  }

  public Map<String, NetworkServer> getNetworkServerMap() {
    return networkServerMap;
  }
}