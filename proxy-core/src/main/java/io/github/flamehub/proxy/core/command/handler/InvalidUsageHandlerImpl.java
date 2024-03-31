package io.github.flamehub.proxy.core.command.handler;

import com.velocitypowered.api.command.CommandSource;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invalidusage.InvalidUsage;
import dev.rollczi.litecommands.invalidusage.InvalidUsageHandler;
import dev.rollczi.litecommands.invocation.Invocation;
import io.github.flamehub.proxy.core.locale.VelocityMessagesService;
import io.github.flamehub.proxy.core.text.TextBuilder;

import java.util.List;

public final class InvalidUsageHandlerImpl implements InvalidUsageHandler<CommandSource> {

    private final VelocityMessagesService messagesService;

    public InvalidUsageHandlerImpl(VelocityMessagesService messagesService) {
        this.messagesService = messagesService;
    }

    @Override
    public void handle(Invocation<CommandSource> invocation, InvalidUsage<CommandSource> commandSourceInvalidUsage, ResultHandlerChain<CommandSource> resultHandlerChain) {
        CommandSource sender = invocation.sender();
        List<String> schematics = commandSourceInvalidUsage.getSchematic().all();
        String message = this.messagesService.getMessage("cmd.invalid.usage");
        String usage = schematics.get(0);
        if (schematics.size() == 1) {
            TextBuilder.builder().text(message).placeholder("{CORRECT_USAGE}", usage).send(sender);
            return;
        }

        TextBuilder.builder().text(message).placeholder("{CORRECT_USAGE}", "").send(sender);
        for (String sch : schematics) {
            TextBuilder.builder()
                    .text(this.messagesService.getMessage("cmd.invalid.usage.multiple"))
                    .placeholder("{CORRECT_USAGE}", sch)
                    .send(sender);
        }
    }
}