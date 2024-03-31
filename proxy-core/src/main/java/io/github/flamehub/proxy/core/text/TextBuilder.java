package io.github.flamehub.proxy.core.text;

import com.velocitypowered.api.command.CommandSource;

import java.util.*;

public final class TextBuilder {

    private final List<String> text = new ArrayList<>();
    private final Map<String, Object> placeholders = new HashMap<>();

    public static TextBuilder builder() {
        return new TextBuilder();
    }

    public TextBuilder text(String message) {
        this.text.add(message);
        return this;
    }

    public TextBuilder text(List<String> message) {
        this.text.addAll(message);
        return this;
    }

    public TextBuilder text(String... message) {
        this.text.addAll(List.of(message));
        return this;
    }

    public TextBuilder placeholder(String from, Object to) {
        this.placeholders.put(from, to);
        return this;
    }

    public List<String> build() {
        if (!this.placeholders.isEmpty()) {
            List<String> replacedMessages = new ArrayList<>();

            for (String message : this.text) {

                String messageToReplace = message;
                for (Map.Entry<String, Object> entry : this.placeholders.entrySet()) {
                    messageToReplace = messageToReplace.replace(entry.getKey(), entry.getValue().toString());
                }

                replacedMessages.add(messageToReplace);

            }

            return replacedMessages;
        }

        return this.text;
    }

    public void send(CommandSource commandSender) {
        send(Collections.singletonList(commandSender));
    }


    public void send(Collection<CommandSource> receivers) {
        List<String> messages = build();
        if (receivers.isEmpty() || messages.isEmpty()) {
            return;
        }

        for (CommandSource commandSender : receivers) {

            for (String message : messages) {
                commandSender.sendMessage(TextUtil.parse(message));
            }

        }
    }

    public String firstLine() {
        return build().get(0);
    }

}
