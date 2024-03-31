import io.github.flamehub.commons.util.DiscordWebhook;

import java.awt.*;
import java.time.Instant;

public class Test {

    public static void main(String[] args) {
        DiscordWebhook discordWebhook = new DiscordWebhook("https://discord.com/api/webhooks/1189227835957727272/mAEpnPfhc_Rr9GojANyznQfxT692DU9pfZoZrY9EQi9KLI80M4cId_fvROtrvSoi9SQf");
        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
        embed.setTitle("Test");
        embed.setColor(Color.CYAN);
        embed.setDescription("jebac dujke ez ez ez ez");
        embed.setTimestamp(Instant.now().toString());
        discordWebhook.addEmbed(embed);
        discordWebhook.execute();
    }

}
