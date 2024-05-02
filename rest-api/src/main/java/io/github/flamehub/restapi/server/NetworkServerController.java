package io.github.flamehub.restapi.server;

import io.github.flamehub.commons.server.NetworkServer;
import io.github.flamehub.commons.server.NetworkServerCache;
import io.github.flamehub.commons.server.exception.NetworkServerNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class NetworkServerController {

    private final NetworkServerCache networkServerCache;

    @Autowired
    NetworkServerController(NetworkServerCache networkServerCache) {
        this.networkServerCache = networkServerCache;
    }

    @GetMapping("/network/server/all")
    List<NetworkServer> getServers() {
        return this.networkServerCache.sortedValues(this.networkServerCache.values()).stream().toList();
    }

    @GetMapping("/network/server/find/{name}")
    NetworkServer getServer(final @PathVariable("name") String name) {
        return this.networkServerCache.findByName(name).orElseThrow(() -> new NetworkServerNotFoundException("Server not found"));
    }

    @GetMapping("/network/server/playerscount")
    long getPlayersCount() {
        return this.networkServerCache.getPlayersFrom("proxy");
    }

}
