package io.github.flamehub.proxy.core.command.handler;

import com.velocitypowered.api.command.CommandSource;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invalidusage.InvalidUsage;
import dev.rollczi.litecommands.invalidusage.InvalidUsageHandler;
import dev.rollczi.litecommands.invocation.Invocation;
import io.github.flamehub.proxy.core.message.VelocityMessage;
import io.github.flamehub.proxy.core.message.VelocityMessagesService;

import java.util.List;

public final class InvalidUsageHandlerImpl implements InvalidUsageHandler<CommandSource> {

    private final VelocityMessagesService messagesService;

    public InvalidUsageHandlerImpl(VelocityMessagesService messagesService) {
        this.messagesService = messagesService;
    }

    @Override
    public void handle(Invocation<CommandSource> invocation, InvalidUsage<CommandSource> result, ResultHandlerChain<CommandSource> resultHandlerChain) {
        CommandSource sender = invocation.sender();
        List<String> schematics = result.getSchematic().all();

        String message = this.messagesService.getMessage("cmd.invalid.usage");
        String usage = schematics.get(0);
        if (schematics.size() == 1) {
            VelocityMessage.from(message).with("correct_usage", usage).send(sender);
            return;
        }

        VelocityMessage.from(message).with("correct_usage", "").send(sender);
        for (String sch : schematics) {
            this.messagesService.message("cmd.invalid.usage.multiple")
                    .with("correct_usage", sch)
                    .send(sender);
        }
    }
}