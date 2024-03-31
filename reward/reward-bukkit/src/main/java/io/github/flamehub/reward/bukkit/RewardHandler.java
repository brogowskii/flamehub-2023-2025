package io.github.flamehub.reward.bukkit;

import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.text.TextBuilder;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.messenger.packet.PacketHandler;
import io.github.flamehub.reward.api.RewardReceivedPacket;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public final class RewardHandler {

    private final FlameDispatcher flameDispatcher;
    private final RewardConfig config;

    public RewardHandler(FlameDispatcher flameDispatcher, RewardConfig config) {
        this.flameDispatcher = flameDispatcher;
        this.config = config;
    }

    @PacketHandler
    public void handle(RewardReceivedPacket packet) {

        List<String> build = TextBuilder.builder()
                .text(
                        "",
                        " &f{PLAYER} &7odebrał nagrodę za dołączenie na &3discorda&7.",
                        " &7Nasz discord serwerowy: &fhttps://dc.flamehub.pl/",
                        ""
                )
                .placeholder("{PLAYER}", packet.getPlayerName())
                .build();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            for (String s : build) {
                onlinePlayer.sendMessage(TextUtil.legacyColor(s));
            }
        }

        this.flameDispatcher.dispatch(() -> {
            Server server = Bukkit.getServer();
            ConsoleCommandSender consoleSender = Bukkit.getConsoleSender();
            server.dispatchCommand(consoleSender, config.getCommand().replace("{PLAYER}", packet.getPlayerName()));
        });


    }
}
