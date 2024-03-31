package io.github.flamehub.lobby.selector;

import io.github.flamehub.commons.config.MongoConfig;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public final class ServerSelectorConfig extends MongoConfig {

    private Set<ServerSelector> serverSelectors = Set.of(
            new ServerSelector(
                    "lobby",
                    "lobby1",
                    "joinqueue lobby1",
                    "18:00:00 30.07.2024",
                    new ServerSelectorItem(
                            Material.BARRIER,
                            "&#ce0000&ll&#d60b0b&lo&#de1616&lb&#e72121&lb&#ef2b2b&ly&#f73636&l0&#ff4141&l1",
                            List.of(""),
                            11
                    )
            )
    );

    private ServerSelectorItem serverInfoItem = new ServerSelectorItem(
            Material.WRITABLE_BOOK,
            "&8Informacje",
            List.of(
                    "",
                    " &8Chuj wie cos tu bedzie kiedys",
                    ""
            ),
            16
    );

    private ServerSelectorItem lobbySelectorItem = new ServerSelectorItem(
            Material.NETHER_STAR,
            "&8Zmien swoją poczekalnie",
            List.of(
                    "",
                    " &8Zbyt tłoczno na tym lobby?",
                    "",
                    "&7Kliknij tutaj, aby zobaczyć listę",
                    "&7dostępnych serwerów lobby!"
            ),
            25
    );

    public ServerSelectorConfig() {
    }

    public ServerSelectorConfig(String id) {
        super(id);
    }

    @Nullable
    public ServerSelectorItem getLobbySelectorItem() {
        return lobbySelectorItem;
    }

    @Nullable
    public ServerSelectorItem getServerInfoItem() {
        return serverInfoItem;
    }

    public Set<ServerSelector> getServerSelectors() {
        return serverSelectors;
    }


}

