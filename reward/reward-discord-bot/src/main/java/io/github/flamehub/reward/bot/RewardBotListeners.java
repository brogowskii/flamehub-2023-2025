package io.github.flamehub.reward.bot;

import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.player.NetworkPlayer;
import io.github.flamehub.commons.network.player.NetworkPlayerCache;
import io.github.flamehub.commons.server.NetworkServerCache;
import io.github.flamehub.commons.util.TimeUtil;
import io.github.flamehub.reward.api.RewardReceivedEntry;
import io.github.flamehub.reward.api.RewardReceivedEntryRepository;
import io.github.flamehub.reward.api.RewardReceivedPacket;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.Instant;
import java.util.List;

public final class RewardBotListeners extends ListenerAdapter {

    private final RewardReceivedEntryRepository rewardReceivedEntryRepository;

    private final NetworkPlayerCache networkPlayerCache;
    private final NetworkServerCache networkServerCache;
    private final RedisMessenger redisMessenger;

    public RewardBotListeners(RewardReceivedEntryRepository rewardReceivedEntryRepository, NetworkPlayerCache networkPlayerCache, NetworkServerCache networkServerCache, RedisMessenger redisMessenger) {
        this.rewardReceivedEntryRepository = rewardReceivedEntryRepository;
        this.networkPlayerCache = networkPlayerCache;
        this.networkServerCache = networkServerCache;
        this.redisMessenger = redisMessenger;
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        SlashCommandData slash = Commands.slash("reward", "Wiadomość od nagrody");
        DefaultMemberPermissions adminPermission = DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR);
        slash.setDefaultPermissions(adminPermission);
        event.getGuild()
                .updateCommands()
                .addCommands(slash)
                .queue();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("reward")) {

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Odbierz nagrodę");
            embedBuilder.setColor(Color.RED);
            embedBuilder.setFooter("FlameHub.pl -> Nagrody | " + TimeUtil.formatDate(Instant.now()));
            embedBuilder.setDescription(
                    "> Wybierz tryb na którym chcesz odebrać swoją nagrodę." +
                            " Pamiętaj aby wpisać **poprawny nick**, ponieważ na 1 konto discord nagrodę możesz odebrać **tylko raz**!"
            );
            embedBuilder.setImage("https://i.imgur.com/jvQWJ3f.png");

            event.replyEmbeds(embedBuilder.build())
                    .addActionRow(
                            StringSelectMenu.create("reward-choose-server")
                                    .addOptions(SelectOption.of("Boxpvp", "boxpvp")
                                            .withDescription("Kliknij tutaj aby otrzymać nagrodę na tym trybie!")
                                            .withEmoji(Emoji.fromFormatted("🎀")))
                                    .addOptions(SelectOption.of("Skypvp", "skypvp")
                                            .withDescription("Kliknij tutaj aby otrzymać nagrodę na tym trybie!")
                                            .withEmoji(Emoji.fromFormatted("🎀")))
                                    .build())
                    .queue();
        }
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (event.getComponentId().equalsIgnoreCase("reward-choose-server")) {

            TextInput nickName = TextInput.create("reward-content", "Nick", TextInputStyle.PARAGRAPH)
                    .setRequired(true)
                    .setPlaceholder("Wpisz tutaj swój nick!")
                    .setMinLength(3)
                    .setMaxLength(16)
                    .build();

            Modal modal = Modal.create("reward-modal:" + event.getValues().get(0), "FlameHub.pl -> Nagroda")
                    .addActionRow(nickName)
                    .build();

            event.replyModal(modal).queue();
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (event.getModalId().startsWith("reward-modal")) {
            String nickName = event.getValue("reward-content").getAsString();
            String server = event.getModalId().split(":")[1];

            User user = event.getUser();
            if (this.rewardReceivedEntryRepository.loadByUserIdAndServerCategory(user.getIdLong(), server) != null) {
                event.reply("Odebrałeś już nagrodę na to konto discord!")
                        .setEphemeral(true)
                        .queue();
                return;
            }

            if (this.rewardReceivedEntryRepository.loadByPlayerNameAndServerCategory(nickName, server) != null) {
                event.reply("Odebrałeś już nagrodę na ten nick!")
                        .setEphemeral(true)
                        .queue();
                return;
            }

            NetworkPlayer networkPlayer = this.networkPlayerCache.findByName(nickName);
            if (networkPlayer == null) {
                event.reply("Nie ma Cię na serwerze, musisz być online aby odebrać nagrodę!")
                        .setEphemeral(true)
                        .queue();
                return;
            }

            if (!networkPlayer.getServerCategory().equalsIgnoreCase(server)) {
                event.reply("Nie ma Cię na serwerze, musisz być online aby odebrać nagrodę!")
                        .setEphemeral(true)
                        .queue();
                return;
            }

            RewardReceivedEntry rewardReceivedEntry = new RewardReceivedEntry(nickName, user.getIdLong(), server);
            this.rewardReceivedEntryRepository.save(rewardReceivedEntry);
            this.redisMessenger.publish(networkPlayer.getServer(), new RewardReceivedPacket(networkPlayer.getName()));
            event.reply("Pomyślnie odebrałeś swoją nagrodę!")
                    .setEphemeral(true)
                    .queue();

        }


    }
}