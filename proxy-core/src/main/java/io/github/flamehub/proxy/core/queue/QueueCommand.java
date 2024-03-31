package io.github.flamehub.proxy.core.queue;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.proxy.core.ProxyCore;
import io.github.flamehub.proxy.core.text.TextUtil;
import io.github.flamehub.commons.server.NetworkServer;
import io.github.flamehub.commons.server.NetworkServerCache;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Command(name = "queue")
@Permission("server.velocity.commands.queue")
public class QueueCommand {


    private final ProxyServer proxyServer;
    private final QueueService queueService;
    private final NetworkServerCache networkServerCache;
    private final QueueRedirectService queueRedirectService;


    public QueueCommand(ProxyServer proxyServer, NetworkServerCache networkServerCache, QueueService queueService, QueueRedirectService queueRedirectService) {
        this.proxyServer = proxyServer;
        this.networkServerCache = networkServerCache;
        this.queueService = queueService;
        this.queueRedirectService = queueRedirectService;
    }

    @Execute(name = "status")
    void exec(@Context CommandSource commandSource) {

        commandSource.sendMessage(TextUtil.parse("&7Lista kolejek:"));
        for (Map.Entry<String, LinkedList<String>> entry : this.queueService.getQueues().entrySet()) {
            int size = entry.getValue().size();
            commandSource.sendMessage(TextUtil.parse("&8- &a" + entry.getKey() + " &7(&f" + size + "os&7)"));
        }

    }

    @Execute(name = "join")
    void exec(@Context Player player, @Arg String queue) {

        List<NetworkServer> serversByCategory = this.networkServerCache.findServersByCategory(queue);
        if (serversByCategory == null || serversByCategory.isEmpty()) {
            player.sendMessage(TextUtil.parse("&cBrak serwerów."));
            return;
        }

        this.queueService.add(queue, player.getUsername());
        player.createConnectionRequest(
                ProxyCore
                        .getInstance()
                        .getProxyServer()
                        .getServer("queue")
                        .get()
                )
                .fireAndForget();
        player.sendMessage(TextUtil.parse("&aDołączono do kolejki: &2" + queue));

    }

    @Execute(name = "instantMoveSafe")
    public void instantMoveSafe(@Context CommandSource source, @Arg String queue) {
        int i = 0;
        for (String entry : this.queueService.getAllPlayers(queue)) {
            if (this.queueRedirectService.move(entry, queue)) {
                i++;
            }
        }

        source.sendMessage(TextUtil.parse("&aPomyślnie przeniesiono " + i + " graczy z kolejki " + queue));

    }

    @Execute(name = "instantMove")
    public void instantMove(@Context CommandSource source, @Arg String queue) {
        int i = 0;
        for (String entry : this.queueService.getAllPlayers(queue)) {
            Optional<Player> optionalPlayer = this.proxyServer.getPlayer(entry);
            if (optionalPlayer.isPresent()) {
                Player player = optionalPlayer.get();
                player.createConnectionRequest(this.proxyServer.getServer(this.networkServerCache.getLeastCrowded(queue).getName()).get()).fireAndForget();
                i++;
            }
        }

        source.sendMessage(TextUtil.parse("&aPomyślnie przeniesiono " + i + " graczy z kolejki " + queue));

    }

}
