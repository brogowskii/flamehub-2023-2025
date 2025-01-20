package io.github.flamehub.commons.server;

import java.util.Optional;
import java.util.logging.Logger;

public final class NetworkServerLoader {

  private final Logger logger;

  private final NetworkServerCache networkServerCache;
  private final NetworkServerRepository networkServerRepository;
  private final String currentServerName;

  public NetworkServerLoader(
      Logger logger,
      NetworkServerCache networkServerCache,
      NetworkServerRepository networkServerRepository,
      String currentServerName
  ) {
    this.logger = logger;
    this.networkServerCache = networkServerCache;
    this.networkServerRepository = networkServerRepository;
    this.currentServerName = currentServerName;
  }

  public void load() {
    networkServerRepository.loadAll().forEach(networkServerCache::add);
    Optional<NetworkServer> currentOptional = networkServerCache.findByName(
        currentServerName);
    currentOptional.ifPresent(networkServerCache::setCurrent);
    logger.info(
        "Pomyślnie załadowano " + networkServerCache.values().size() + " serwerów w sieci.");
  }

}
