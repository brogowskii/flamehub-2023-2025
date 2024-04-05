package io.github.flamehub.proxy.core.message;

import com.velocitypowered.api.command.CommandSource;
import io.github.flamehub.commons.message.Message;
import io.github.flamehub.proxy.core.util.TextUtil;

import java.util.Collection;
import java.util.List;

public class VelocityMessage extends Message {

    public static VelocityMessage from(String message) {
        return new VelocityMessage().add(message);
    }

    public static VelocityMessage from(List<String> messages) {
        return new VelocityMessage().add(messages);
    }

    public static VelocityMessage from(String... messages) {
        return new VelocityMessage().add(messages);
    }

    @Override
    public VelocityMessage add(String message) {
        return (VelocityMessage) super.add(message);
    }

    @Override
    public VelocityMessage add(List<String> messages) {
        return (VelocityMessage) super.add(messages);
    }

    @Override
    public VelocityMessage add(String... messages) {
        return (VelocityMessage) super.add(messages);
    }

    @Override
    public VelocityMessage with(String from, Object to) {
        return (VelocityMessage) super.with(from, to);
    }

    public void send(CommandSource commandSource) {
        apply().forEach(s -> commandSource.sendMessage(TextUtil.parse(s)));
    }

    public void send(Collection<CommandSource> commandSources) {
        for (CommandSource source : commandSources) {
            apply().forEach(s -> source.sendMessage(TextUtil.parse(s)));
        }
    }

}
