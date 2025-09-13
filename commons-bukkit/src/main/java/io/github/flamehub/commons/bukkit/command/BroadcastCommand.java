package io.github.flamehub.commons.bukkit.command;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.join.Join;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.network.message.NetworkMessageFilter;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.commons.network.message.NetworkMessageType;
import io.github.flamehub.commons.server.NetworkServerFacade;
import java.util.List;
import org.bukkit.command.CommandSender;

@Command(name = "broadcast", aliases = "bc")
@Permission("flamehub.network.commands.broadcast")
public final class BroadcastCommand {

  private final NetworkMessageService networkMessageService;
  private final NetworkServerFacade networkServerFacade;

  public BroadcastCommand(final NetworkMessageService networkMessageService,
      final NetworkServerFacade networkServerFacade) {
    this.networkMessageService = networkMessageService;
    this.networkServerFacade = networkServerFacade;
  }

  @Execute(name = "chat")
  void chat(@Context final CommandSender sender, @Join final String content) {

    networkMessageService.send(
        List.of(
            "    &8[&3⚠&8] &#1D9AE2&lᴏ&#2D9FE0&lɢ&#3DA3DE&lʟ&#4EA8DC&lᴏ&#5EADDA&ls&#5EADDA&lᴢ&#4EA8DC&lᴇ&#3DA3DE&lɴ&#2D9FE0&lɪ&#1D9AE2&lᴇ &8[&3⚠&8] &r",
            "&8» &r" + content
        ),
        NetworkMessageFilter.builder()
            .targetServerCategory(networkServerFacade.getCurrent().getCategory())
            .build(),
        NetworkMessageType.CHAT
    );

  }

  @Execute(name = "title")
  void title(@Context final CommandSender sender, @Join final String content) {

    networkMessageService.send(
        List.of(
            "&8[&3⚠&8] &#1D9AE2&lᴏ&#2D9FE0&lɢ&#3DA3DE&lʟ&#4EA8DC&lᴏ&#5EADDA&ls&#5EADDA&lᴢ&#4EA8DC&lᴇ&#3DA3DE&lɴ&#2D9FE0&lɪ&#1D9AE2&lᴇ &8[&3⚠&8]",
            content),
        NetworkMessageFilter.builder()
            .targetServerCategory(networkServerFacade.getCurrent().getCategory())
            .build(),
        NetworkMessageType.TITLE
    );

  }

  @Execute(name = "actionbar")
  void actionbar(@Context final CommandSender sender, @Join final String content) {

    networkMessageService.send(
        content,
        NetworkMessageFilter.builder()
            .targetServerCategory(networkServerFacade.getCurrent().getCategory())
            .build(),
        NetworkMessageType.ACTION_BAR
    );

  }


}
