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
    this.networkServerRepository.loadAll().forEach(this.networkServerCache::add);
    Optional<NetworkServer> currentOptional = this.networkServerCache.findByName(
        this.currentServerName);
    currentOptional.ifPresent(this.networkServerCache::setCurrent);
    this.logger.info(
        "Pomyślnie załadowano " + this.networkServerCache.values().size() + " serwerów w sieci.");
  }

}
