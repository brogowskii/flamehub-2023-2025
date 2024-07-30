package io.github.flamehub.restapi.player;

import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.restapi.player.exceptions.NetworkPlayerNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
class NetworkPlayerController {

  private final NetworkPlayerCache networkPlayerCache;

  @Autowired
  NetworkPlayerController(final NetworkPlayerCache networkPlayerCache) {
    this.networkPlayerCache = networkPlayerCache;
  }

  @GetMapping("/network/player/all")
  List<NetworkPlayer> getPlayers() {
    return new ArrayList<>(this.networkPlayerCache.values());
  }

  @GetMapping("/network/player/find/{nickname}")
  NetworkPlayer getPlayer(final @PathVariable("nickname") String nickname) {
    Optional<NetworkPlayer> networkPlayerOptional = Optional.ofNullable(
        this.networkPlayerCache.findByName(nickname));
    return networkPlayerOptional.orElseThrow(
        () -> new NetworkPlayerNotFoundException("Player not found"));
  }
}
