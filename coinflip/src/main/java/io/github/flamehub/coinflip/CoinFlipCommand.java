package io.github.flamehub.coinflip;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import io.github.flamehub.coinflip.user.CoinFlipUser;
import io.github.flamehub.coinflip.user.CoinFlipUserCache;
import io.github.flamehub.commons.bukkit.CommonsPlugin;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.HexUtil;
import io.github.flamehub.commons.config.FlameConfigService;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.network.message.NetworkMessageFilter;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.commons.network.message.NetworkMessageType;
import java.util.HashMap;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Command(name = "coinflip", aliases = "cf")
public final class CoinFlipCommand {

  private final RedisMessenger redisMessenger;
  private final NetworkMessageService networkMessageService;
  private final FlameConfigService flameConfigService;
  private final CoinFlipConfig coinFlipConfig;
  private final CoinFlipGameCache coinFlipGameCache;
  private final CoinFlipUserCache coinFlipUserCache;

  public CoinFlipCommand(
      final RedisMessenger redisMessenger,
      final NetworkMessageService networkMessageService,
      final FlameConfigService flameConfigService,
      final CoinFlipConfig coinFlipConfig,
      final CoinFlipGameCache coinFlipGameCache,
      final CoinFlipUserCache coinFlipUserCache
  ) {
    this.redisMessenger = redisMessenger;
    this.networkMessageService = networkMessageService;
    this.flameConfigService = flameConfigService;
    this.coinFlipConfig = coinFlipConfig;
    this.coinFlipGameCache = coinFlipGameCache;
    this.coinFlipUserCache = coinFlipUserCache;
  }

  @Execute(name = "setcurrency")
  @Permission("server.skypvp.commands.coinflip.admin.setcurrency")
  void setCurrency(final @Context Player player) {

    final ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
    if (itemInMainHand.getType().isAir()) {
      return;
    }

    coinFlipConfig.setCurrency(itemInMainHand);
    flameConfigService.save(CoinFlipConfig.class);

    BukkitMessage.from(
            "&aUstawiono walutę na &f" + TextUtil.serialize(itemInMainHand.getItemMeta().displayName()))
        .deliver(player);

  }


  @Execute(name = "gry")
  void games(final @Context Player player) {

    final CoinFlipGui coinFlipGui = new CoinFlipGui(
        redisMessenger,
        networkMessageService,
        coinFlipConfig,
        coinFlipGameCache,
        coinFlipUserCache
    );
    coinFlipGui.open(player);

  }

  @Execute(name = "stworz")
  void create(final @Context Player player, @Arg final int bet) {

    if (bet < 10) {
      BukkitMessage.from("&cStawka musi wynosić przynajmniej 10 fragmentów").deliver(player);
      return;
    }

    if (bet > 500) {
      BukkitMessage.from("&cStawka nie może być większa niż 500").deliver(player);
      return;
    }

    if (coinFlipGameCache.getPlayerGames(player.getUniqueId()) > 0) {
      BukkitMessage.from("&cNie możesz stworzyć więcej niż jednej gry!").deliver(player);
      return;
    }

    final ItemStack currency = coinFlipConfig.getCurrency().clone();
    currency.setAmount(bet);
    if (!player.getInventory().containsAtLeast(currency, bet)) {
      BukkitMessage.from("&cNie posiadasz wystarczającej ilości waluty aby stworzyć rzut monetą.")
          .deliver(player);
      return;
    }

    final CoinFlipGame coinFlipGame = new CoinFlipGame(
        new CoinFlipPlayer(player.getName(), player.getUniqueId()), bet);
    coinFlipGameCache.put(coinFlipGame.getId(), coinFlipGame);

    currency.setAmount(bet);
    player.getInventory().removeItem(currency);

    final String coloredCreatorName = HexUtil.interpolateColors(player.getName(), "#CB2EBA",
        "#EE35DA", false);
    networkMessageService.sendAsync(
        List.of(
            "",
            "&5☯ &8| &#CB2EBA&l/" + CoinFlipConstants.COINFLIP_PREFIX + " &8▶ &fGracz "
                + coloredCreatorName + " &fstworzył",
            "&frzut monetą o stawce &dx&l" + bet + " &ffragmentów!",
            ""
        ),
        NetworkMessageFilter.builder()
            .idForHide("coinflip")
            .targetServerCategory(
                CommonsPlugin.getInstance().getNetworkServerFacade().getCurrent().getCategory())
            .build(),
        NetworkMessageType.CHAT
    );


  }

  @Execute(name = "odbierz")
  void receive(final @Context Player player) {
    final CoinFlipUser coinFlipUser = coinFlipUserCache.findByUniqueId(player.getUniqueId());
    if (coinFlipUser.getDeposit() <= 0) {
      BukkitMessage.from("&cNie masz waluty do odebrania").deliver(player);
      return;
    }

    final ItemStack currency = coinFlipConfig.getCurrency().clone();
    currency.setAmount(coinFlipUser.getDeposit());

    final HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(currency);
    final int totalLeftover = leftover.values().stream()
        .mapToInt(ItemStack::getAmount)
        .sum();
    final int actuallyGiven = coinFlipUser.getDeposit() - totalLeftover;

    coinFlipUserCache.update(player.getUniqueId(), user -> {
      user.setDeposit(totalLeftover);
    });

    BukkitMessage.from("&aPomyślnie wypłacono &fx" + actuallyGiven).deliver(player);
    if (totalLeftover > 0) {
      BukkitMessage.from("&cPozostało &f" + totalLeftover + " &cz powodu braku miejsca")
          .deliver(player);
    }
  }

}