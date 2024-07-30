package io.github.flamehub.code;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.commons.bukkit.config.FlameConfigRefresher;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.config.FlameConfigService;
import io.github.flamehub.commons.util.TimeUtil;
import io.github.flamehub.timeplayed.user.TimePlayedUser;
import io.github.flamehub.timeplayed.user.TimePlayedUserCache;
import java.time.Duration;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(name = "code", aliases = {"kod", "kody"})
public final class CodeCommand extends FlameConfigRefresher {


  private final FlameDispatcher flameDispatcher;
  private final FlameConfigService flameConfigService;
  private final CodeConfig codeConfig;
  private final CodeUserCache codeUserCache;
  private final CodeUserRepository codeUserRepository;
  private final TimePlayedUserCache timePlayedUserCache;

  public CodeCommand(TimePlayedUserCache timePlayedUserCache, FlameDispatcher flameDispatcher,
      FlameConfigService flameConfigService, CodeConfig codeConfig, CodeUserCache codeUserCache,
      CodeUserRepository codeUserRepository) {
    super(flameConfigService, CodeConfig.class);
    this.timePlayedUserCache = timePlayedUserCache;
    this.flameDispatcher = flameDispatcher;
    this.flameConfigService = flameConfigService;
    this.codeConfig = codeConfig;
    this.codeUserCache = codeUserCache;
    this.codeUserRepository = codeUserRepository;
  }

  @Execute(name = "reload")
  @Permission("server.commands.code.reload")
  void reload(@Context CommandSender sender) {
    super.refreshConfigRemote(sender);
  }

  @Execute
  void execute(@Context Player player, @Arg("kod") String codeString) {

    Code code = this.codeConfig.findByName(codeString);
    if (code == null) {
      BukkitMessage.from("&cTen kod nie istnieje!").send(player);
      return;
    }

    CodeUser codeUser = this.codeUserCache.findByUniqueId(player.getUniqueId());
    if (codeUser.getReceivedCodes().contains(code.getName())) {
      BukkitMessage.from("&cWykorzystałeś już ten kod!").send(player);
      return;
    }

    if (code.getRequiredTime() != null) {

      TimePlayedUser timePlayedUser = this.timePlayedUserCache.findByUniqueId(player.getUniqueId());
      long spendTime = timePlayedUser.getSpendTime();
      Duration duration = TimeUtil.parseTime(code.getRequiredTime());
      long millis = duration.toMillis();

      if (millis > spendTime) {
        BukkitMessage.from("&cDo użycia tego kodu potrzebujesz spędzić na serwerze jeszcze: &4"
            + TimeUtil.formatTimeSimple(millis - spendTime)).send(player);
        return;
      }

    }

    codeUser.getReceivedCodes().add(code.getName());
    this.flameDispatcher.dispatchAsync(() -> this.codeUserRepository.save(codeUser));

    for (String command : code.getCommands()) {
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
          command.replace("{PLAYER}", player.getName()));
    }

    if (code.getBroadcast() != null && !code.getBroadcast().isEmpty()) {
      for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
        code.getBroadcast().forEach(
            s -> onlinePlayer.sendMessage(TextUtil.parse(s.replace("{PLAYER}", player.getName()))));
      }
    }

    BukkitMessage.from("&aPomyślnie aktywowano kod!").send(player);
  }

}
