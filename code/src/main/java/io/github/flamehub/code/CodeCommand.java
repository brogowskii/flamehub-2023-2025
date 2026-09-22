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
import io.github.flamehub.commons.server.NetworkServerContext;
import io.github.flamehub.commons.util.TimeUtil;
import io.github.flamehub.reward.api.RewardReceivedEntryRepository;
import io.github.flamehub.timeplayed.user.TimePlayedUser;
import io.github.flamehub.timeplayed.user.TimePlayedUserCache;
import java.time.Duration;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(name = "code", aliases = {"kod", "kody"})
final class CodeCommand extends FlameConfigRefresher {

  private final FlameDispatcher flameDispatcher;
  private final CodeConfig codeConfig;
  private final CodeUserCache codeUserCache;
  private final CodeUserRepository codeUserRepository;
  private final TimePlayedUserCache timePlayedUserCache;
  private final RewardReceivedEntryRepository rewardEntryRepository;

  CodeCommand(
      final TimePlayedUserCache timePlayedUserCache,
      final FlameDispatcher flameDispatcher,
      final FlameConfigService flameConfigService,
      final CodeConfig codeConfig,
      final CodeUserCache codeUserCache,
      final CodeUserRepository codeUserRepository,
      final RewardReceivedEntryRepository rewardEntryRepository
  ) {
    super(flameConfigService, CodeConfig.class);
    this.timePlayedUserCache = timePlayedUserCache;
    this.flameDispatcher = flameDispatcher;
    this.codeConfig = codeConfig;
    this.codeUserCache = codeUserCache;
    this.codeUserRepository = codeUserRepository;
    this.rewardEntryRepository = rewardEntryRepository;
  }

  @Execute(name = "reload")
  @Permission("server.commands.code.reload")
  void reload(@Context final CommandSender sender) {
    refreshAndBroadcast(sender);
  }

  @Execute
  void execute(@Context final Player player, @Arg("kod") final String codeString) {

    final Code code = codeConfig.findByName(codeString);
    if (code == null) {
      BukkitMessage.from("&cTen kod nie istnieje!").deliver(player);
      return;
    }

    final CodeUser codeUser = codeUserCache.findByUniqueId(player.getUniqueId());
    if (codeUser.getReceivedCodes().contains(code.getName())) {
      BukkitMessage.from("&cWykorzystałeś już ten kod!").deliver(player);
      return;
    }

    if (code.getRequiredTime() != null) {

      final TimePlayedUser timePlayedUser = timePlayedUserCache.findByUniqueId(
          player.getUniqueId());
      final long spendTime = timePlayedUser.getSpendTime();
      final Duration duration = TimeUtil.parseTime(code.getRequiredTime());
      final long millis = duration.toMillis();

      if (millis > spendTime) {
        BukkitMessage.from("&cDo użycia tego kodu potrzebujesz spędzić na serwerze jeszcze: &4"
            + TimeUtil.formatTimeSimple(millis - spendTime)).deliver(player);
        return;
      }

    }

    if (rewardEntryRepository.loadByPlayerNameAndServerCategory(player.getName(), NetworkServerContext.CURRENT_CATEGORY) == null) {
      BukkitMessage.from(
          "&cAby użyć tego kodu musisz najpierw odebrać nagrodę discord!",
              "&cNasz discord: &4https://dc.flamehub.pl/"
          ).deliver(player);
      return;
    }

    codeUser.getReceivedCodes().add(code.getName());
    flameDispatcher.dispatchAsync(() -> codeUserRepository.save(codeUser));

    for (final String command : code.getCommands()) {
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
          command.replace("{PLAYER}", player.getName()));
    }

    if (code.getBroadcast() != null && !code.getBroadcast().isEmpty()) {
      for (final Player onlinePlayer : Bukkit.getOnlinePlayers()) {
        code.getBroadcast().forEach(
            s -> onlinePlayer.sendMessage(TextUtil.parse(s.replace("{PLAYER}", player.getName()))));
      }
    }

    BukkitMessage.from("&aPomyślnie aktywowano kod!").deliver(player);
  }

}
