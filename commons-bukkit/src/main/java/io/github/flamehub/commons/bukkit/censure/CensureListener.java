package io.github.flamehub.commons.bukkit.censure;

import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public final class CensureListener implements Listener {

    private final CensureConfig config;

    public CensureListener(CensureConfig config) {
        this.config = config;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {

        Player player = event.getPlayer();
        if (player.hasPermission("censure.bypass")) {
            return;
        }

        String message = event.getMessage();
        if (event.getMessage().startsWith("https://discord.com/oauth2/authorize?")) {
            event.setCancelled(true);
            return;
        }

        if (StringUtils.containsIgnoreCase(message, "banbox")) {
            BukkitMessage.from("&cZiomeczku, takie chujowe serwery to możesz reklamować gdzie indziej :)").send(player);
            event.setCancelled(true);
            return;
        }

        if (!StringUtils.containsIgnoreCase(message, "flamehub")) {
            if (StringUtils.containsIgnoreCase(message, ".pl") || StringUtils.containsIgnoreCase(message, ".eu") || StringUtils.containsIgnoreCase(message, ".net") || StringUtils.containsIgnoreCase(message, ".aternos")) {
                event.setCancelled(true);
                return;
            }
        }

        for (String s : this.config.getCensureReplacementList()) {
            message = StringUtils.replaceIgnoreCase(message, s, replaceWordWithStars(s));
        }

        event.setMessage(message);

    }

    private static String replaceWordWithStars(String word) {
        return "*".repeat(word.length());
    }


}
