package io.github.flamehub.commons.bukkit.message;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.command.CommandSender;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.message.MessagesRepository;

@Command(name = "messages")
@Permission("server.commands.messagesreload")
public final class MessagesReloadCommand {

    private final MessagesRepository repository;

    public MessagesReloadCommand(MessagesRepository repository) {
        this.repository = repository;
    }

    @Execute(name = "execute")
    void reload(@Context CommandSender sender) {
        this.repository.loadMessages();
        sender.sendMessage(TextUtil.parse("&aSuccessfully reloaded server messages."));
    }

}
