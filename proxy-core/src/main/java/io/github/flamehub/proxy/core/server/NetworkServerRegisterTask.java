package io.github.flamehub.proxy.core.server;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import io.github.flamehub.commons.server.NetworkServer;
import io.github.flamehub.commons.server.NetworkServerFacade;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import net.kyori.adventure.text.Component;

public final class NetworkServerRegisterTask implements Runnable {

  private static final Duration HEARTBEAT_TIMEOUT = Duration.ofSeconds(2);
  private static final int DEFAULT_PORT = 25565;

  private final ProxyServer proxyServer;
  private final NetworkServerFacade networkServerFacade;

  public NetworkServerRegisterTask(final ProxyServer proxyServer,
      final NetworkServerFacade networkServerFacade) {
    this.proxyServer = proxyServer;
    this.networkServerFacade = networkServerFacade;
  }

  @Override
  public void run() {
    final Instant now = Instant.now();

    for (final NetworkServer value : networkServerFacade.values()) {
      if ("velocity".equals(value.getCategory())) {
        continue;
      }

      final Instant lastUpdate = value.getStatistics().getLastUpdate();
      final boolean alive = lastUpdate != null
          && Duration.between(lastUpdate, now).compareTo(HEARTBEAT_TIMEOUT) <= 0;

      final String serverName = value.getName();
      final Optional<RegisteredServer> existing = proxyServer.getServer(serverName);

      if (alive) {
        if (existing.isEmpty()) {
          final HostPort hp = parseHostPort(value.getIp(), DEFAULT_PORT);
          final InetSocketAddress address = InetSocketAddress.createUnresolved(hp.host(), hp.port());

          proxyServer.registerServer(new ServerInfo(serverName, address));
          proxyServer.getConsoleCommandSource().sendMessage(
              Component.text("[RegisterTask] Zarejestrowano " + serverName + " @ " + hp.host() + ":"
                  + hp.port())
          );
        }
      } else {
        if (existing.isPresent()) {
          proxyServer.unregisterServer(existing.get().getServerInfo());
          proxyServer.getConsoleCommandSource().sendMessage(
              Component.text(
                  "[RegisterTask] Wyrejestrowano " + serverName + " (brak heartbeat > 2s)")
          );
        }
      }
    }
  }

  private record HostPort(String host, int port) {

  }

  private static HostPort parseHostPort(final String input, final int defaultPort) {
    if (input == null || input.isEmpty()) {
      throw new IllegalArgumentException("Empty IP string");
    }
    final String s = input.trim();

    if (s.startsWith("[")) {
      final int end = s.indexOf(']');
      if (end < 0) {
        throw new IllegalArgumentException("Invalid IPv6 format: " + s);
      }
      final String host = s.substring(1, end);
      int port = defaultPort;
      if (end + 1 < s.length() && s.charAt(end + 1) == ':') {
        port = parsePort(s.substring(end + 2), defaultPort);
      }
      return new HostPort(host, port);
    }

    final int lastColon = s.lastIndexOf(':');
    if (lastColon > -1 && s.indexOf(':') == lastColon) {
      final String host = s.substring(0, lastColon);
      final int port = parsePort(s.substring(lastColon + 1), defaultPort);
      return new HostPort(host, port);
    }

    return new HostPort(s, defaultPort);
  }

  private static int parsePort(final String portStr, final int fallback) {
    try {
      final int port = Integer.parseInt(portStr);
      if (port >= 1 && port <= 65535) {
        return port;
      }
    } catch (final NumberFormatException ignored) {
    }
    return fallback;
  }
}
