package io.github.flamehub.wallet;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.triumphteam.gui.guis.Gui;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.commons.bukkit.message.BukkitMessagesService;
import io.github.flamehub.commons.bukkit.text.TextBuilder;
import io.github.flamehub.commons.bukkit.text.TextUtil;
import io.github.flamehub.commons.bukkit.util.FlameItemBuilder;
import io.github.flamehub.commons.bukkit.util.GuiHelper;
import io.github.flamehub.commons.network.message.NetworkMessageService;
import io.github.flamehub.commons.network.message.NetworkMessageType;
import io.github.flamehub.commons.util.RoundUtil;
import io.github.flamehub.wallet.item.WalletOffer;
import io.github.flamehub.wallet.item.WalletOfferConfig;
import io.github.flamehub.wallet.item.WalletOfferVariant;
import io.github.flamehub.wallet.log.WalletLog;
import io.github.flamehub.wallet.log.WalletLogAction;
import io.github.flamehub.wallet.log.WalletLogBuilder;
import io.github.flamehub.wallet.log.WalletLogRepository;
import io.github.flamehub.wallet.user.WalletUser;
import io.github.flamehub.wallet.user.WalletUserFacade;
import java.math.BigDecimal;
import java.util.List;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

@Command(
    name = "wallet",
    aliases = {
        "portfel",
        "is",
        "itemshop",
        "uslugipremium"
    }
)
final class WalletCommand {

  private final FlameDispatcher flameDispatcher;
  private final NetworkMessageService networkMessageService;
  private final BukkitMessagesService messagesService;
  private final WalletUserFacade walletUserFacade;
  private final WalletOfferConfig walletOfferConfig;
  private final WalletLogRepository walletLogRepository;

  WalletCommand(
      final FlameDispatcher flameDispatcher,
      final NetworkMessageService networkMessageService,
      final BukkitMessagesService messagesService,
      final WalletUserFacade walletUserFacade,
      final WalletOfferConfig walletOfferConfig,
      final WalletLogRepository walletLogRepository
  ) {
    this.flameDispatcher = flameDispatcher;
    this.networkMessageService = networkMessageService;
    this.messagesService = messagesService;
    this.walletUserFacade = walletUserFacade;
    this.walletOfferConfig = walletOfferConfig;
    this.walletLogRepository = walletLogRepository;
  }

  @Execute
  void execute(@Context Player player, @Context WalletUser walletUser) {
    openGui(player, walletUser);
  }

  void openGui(Player player, WalletUser walletUser) {
    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 3f, 1f);
    Gui gui = Gui.gui()
        .title(TextUtil.parse(messagesService.getMessage("wallet.gui.title")))
        .rows(6)
        .disableAllInteractions()
        .create();

    GuiHelper.fillGui6(gui);

    for (WalletOffer walletOffer : walletOfferConfig.getWalletOffers()) {

      int size = walletOffer.getVariants().size();
      final FlameItemBuilder lore = FlameItemBuilder.of(walletOffer.getIcon())
          .glow()
          .name(walletOffer.getOffer())
          .lore(BukkitMessage.from(walletOffer.getLore())
              .with("price", walletOffer.lowestPriceVariant() == null ? "&cBrak wariantów."
                  : walletOffer.lowestPriceVariant().getCost())
              .with("variants_size", size)
              .with("user_money", RoundUtil.round(walletUser.getMoney().doubleValue(), 2))
              .apply());
      if (walletOffer.getCustomModelData() != 0) {
        lore.customModelData(walletOffer.getCustomModelData());
      }
      gui.setItem(walletOffer.getSlot(), lore
          .asGuiItem(event -> {

            if (size > 1) {
              openVariantsGui(player, walletOffer, walletUser);
              return;
            }

            if (walletOffer.getVariants().isEmpty()) {
              return;
            }

            WalletOfferVariant variant = walletOffer.getVariants().get(0);
            openBuyConfirmationGui(player, walletUser, walletOffer, variant);

          }));

    }

    gui.open(player);
  }

  void openVariantsGui(Player player, WalletOffer walletOffer, WalletUser walletUser) {
    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 3f, 1f);
    Gui gui = Gui.gui()
        .rows(3)
        .title(TextUtil.parse(messagesService.getMessage("wallet.selection.gui.title")))
        .disableAllInteractions()
        .create();

    gui.getFiller()
        .fillBorder(FlameItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).name(" ").asGuiItem());

    for (WalletOfferVariant variant : walletOffer.getVariants()) {
      final FlameItemBuilder lore = FlameItemBuilder.of(walletOffer.getIcon())
          .glow()
          .name(variant.getName())
          .amount(variant.getAmount())
          .lore(TextBuilder.builder().text(variant.getLore())
              .placeholder("{PRICE}", variant.getCost())
              .placeholder("{AMOUNT}", variant.getAmount())
              .build());

      if (walletOffer.getCustomModelData() != 0) {
        lore.customModelData(walletOffer.getCustomModelData());
      }
      gui.addItem(lore
          .asGuiItem(event -> openBuyConfirmationGui(player, walletUser, walletOffer, variant)));
    }

    gui.open(player);

  }

  void openBuyConfirmationGui(Player player, WalletUser walletUser, WalletOffer offer,
      WalletOfferVariant variant) {
    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 3f, 1f);
    Gui gui = Gui.gui()
        .rows(3)
        .title(TextUtil.parse(messagesService.getMessage("wallet.confirmation.gui.title")))
        .disableAllInteractions()
        .create();

    gui.getFiller()
        .fill(FlameItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).name(" ").asGuiItem());
    gui.setItem(List.of(0, 1, 2, 9, 10, 11, 18, 19, 20),
        FlameItemBuilder.of(Material.GREEN_STAINED_GLASS_PANE)
            .name("&aPotwierdź kupno")
            .asGuiItem(event -> {

              player.closeInventory();
              walletUserFacade.mutate(walletUser.getUniqueId(),
                  mutator -> {

                    if (!mutator.hasEnough(BigDecimal.valueOf(variant.getCost()))) {
                      messagesService.message("wallet.not.enough.money")
                          .with("user_money",
                              RoundUtil.round(walletUser.getMoney().doubleValue(), 2))
                          .with("money_needed",
                              RoundUtil.round(
                                  variant.getCost() - walletUser.getMoney().doubleValue(), 2))
                          .deliver(player);
                      return;
                    }

                    mutator.subtractMoney(BigDecimal.valueOf(variant.getCost()));

                    networkMessageService.send(
                        PlaceholderAPI.setPlaceholders(player,
                            BukkitMessage.from(variant.getBroadcast())
                                .with("player", player.getName())
                                .with("amount", variant.getAmount())
                                .apply()),
                        NetworkMessageType.CHAT
                    );

                    final WalletLog walletLog = WalletLogBuilder.create()
                        .action(WalletLogAction.BUY)
                        .buyerName(walletUser.getName())
                        .boughtItem(variant.getName() + ":" + variant.getAmount())
                        .amount(variant.getCost())
                        .build();
                    walletLogRepository.save(walletLog);

                    flameDispatcher.dispatch(() -> {
                      Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                          PlaceholderAPI.setPlaceholders(player, variant.getCommand()
                              .replace("{PLAYER}", player.getName())
                              .replace("{AMOUNT}", String.valueOf(variant.getAmount()))));


                    });

                  });
            }));

    gui.setItem(List.of(6, 7, 8, 15, 16, 17, 24, 25, 26),
        FlameItemBuilder.of(Material.RED_STAINED_GLASS_PANE)
            .name("&cAnuluj kupno")
            .asGuiItem(event -> gui.close(player)));

    final FlameItemBuilder lore = FlameItemBuilder.of(offer.getIcon())
        .name(variant.getName())
        .amount(variant.getAmount())
        .lore(TextBuilder.builder().text(variant.getLore())
            .placeholder("{PRICE}", variant.getCost())
            .placeholder("{AMOUNT}", variant.getAmount())
            .build());

    if (offer.getCustomModelData() != 0) {
      lore.customModelData(offer.getCustomModelData());
    }

    gui.setItem(2, 5, lore
        .asGuiItem());

    gui.open(player);

  }

}
