package io.github.flamehub.commons.bukkit.command;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.join.Join;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.network.message.NetworkMessageFilter;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.commons.network.message.NetworkMessageType;
import io.github.flamehub.commons.server.NetworkServerCache;
import java.util.List;
import org.bukkit.command.CommandSender;

@Command(name = "broadcast", aliases = "bc")
@Permission("flamehub.network.commands.broadcast")
public final class BroadcastCommand {

  private final NetworkMessageService networkMessageService;
  private final NetworkServerCache networkServerCache;

  public BroadcastCommand(NetworkMessageService networkMessageService,
      NetworkServerCache networkServerCache) {
    this.networkMessageService = networkMessageService;
    this.networkServerCache = networkServerCache;
  }

  @Execute(name = "chat")
  void chat(@Context CommandSender sender, @Join String content) {

    this.networkMessageService.send(
        List.of(
            "&8[&3⚠&8] &#1D9AE2&lᴏ&#2D9FE0&lɢ&#3DA3DE&lʟ&#4EA8DC&lᴏ&#5EADDA&ls&#5EADDA&lᴢ&#4EA8DC&lᴇ&#3DA3DE&lɴ&#2D9FE0&lɪ&#1D9AE2&lᴇ &8[&3⚠&8] &r",
            "&8» &r" + content
        ),
        NetworkMessageFilter.builder()
            .targetServerCategory(this.networkServerCache.getCurrent().getCategory())
            .build(),
        NetworkMessageType.CHAT
    );

  }

  @Execute(name = "title")
  void title(@Context CommandSender sender, @Join String content) {

    this.networkMessageService.send(
        List.of(
            "&8[&3⚠&8] &#1D9AE2&lᴏ&#2D9FE0&lɢ&#3DA3DE&lʟ&#4EA8DC&lᴏ&#5EADDA&ls&#5EADDA&lᴢ&#4EA8DC&lᴇ&#3DA3DE&lɴ&#2D9FE0&lɪ&#1D9AE2&lᴇ &8[&3⚠&8]",
            content),
        NetworkMessageFilter.builder()
            .targetServerCategory(this.networkServerCache.getCurrent().getCategory())
            .build(),
        NetworkMessageType.TITLE
    );

  }

  @Execute(name = "actionbar")
  void actionbar(@Context CommandSender sender, @Join String content) {

    this.networkMessageService.send(
        content,
        NetworkMessageFilter.builder()
            .targetServerCategory(this.networkServerCache.getCurrent().getCategory())
            .build(),
        NetworkMessageType.ACTION_BAR
    );

  }


}
