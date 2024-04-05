package io.github.flamehub.commons.bukkit.message;

import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.message.Message;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.List;

public class BukkitMessage extends Message {

    public static BukkitMessage from(String message) {
        return new BukkitMessage().add(message);
    }

    public static BukkitMessage from(List<String> messages) {
        return new BukkitMessage().add(messages);
    }

    public static BukkitMessage from(String... messages) {
        return new BukkitMessage().add(messages);
    }

    @Override
    public BukkitMessage add(String message) {
        return (BukkitMessage) super.add(message);
    }

    @Override
    public BukkitMessage add(List<String> messages) {
        return (BukkitMessage) super.add(messages);
    }

    @Override
    public BukkitMessage add(String... messages) {
        return (BukkitMessage) super.add(messages);
    }

    @Override
    public BukkitMessage with(String from, Object to) {
        return (BukkitMessage) super.with(from, to);
    }

    public void send(CommandSender sender) {
        apply().forEach(s -> sender.sendMessage(TextUtil.legacyColor(s)));
    }

    public void send(Collection<CommandSender> senders) {
        for (CommandSender sender : senders) {
            apply().forEach(s -> sender.sendMessage(TextUtil.legacyColor(s)));
        }
    }

}
