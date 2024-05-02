package io.github.flamehub.wallet;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.triumphteam.gui.guis.Gui;
import io.github.flamehub.commons.bukkit.dispatcher.FlameDispatcher;
import io.github.flamehub.commons.bukkit.message.BukkitMessage;
import io.github.flamehub.wallet.api.WalletUserRepository;
import io.github.flamehub.wallet.log.WalletLog;
import io.github.flamehub.wallet.log.WalletLogAction;
import io.github.flamehub.wallet.log.WalletLogRepository;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
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
import io.github.flamehub.wallet.api.WalletUser;
import io.github.flamehub.wallet.user.WalletUserCache;

import java.math.BigDecimal;
import java.util.List;

@Command(
        name = "wallet",
        aliases = {
                "portfel",
                "is",
                "itemshop",
                "uslugipremium"
        }
)
public final class WalletCommand {

    private final FlameDispatcher flameDispatcher;
    private final NetworkMessageService networkMessageService;
    private final BukkitMessagesService messagesService;
    private final WalletUserCache walletUserCache;
    private final WalletOfferConfig walletOfferConfig;
    private final WalletUserRepository walletUserRepository;
    private final WalletLogRepository walletLogRepository;

    public WalletCommand(
            FlameDispatcher flameDispatcher, NetworkMessageService networkMessageService,
            BukkitMessagesService messagesService,
            WalletUserCache walletUserCache,
            WalletOfferConfig walletOfferConfig,
            WalletUserRepository walletUserRepository,
            WalletLogRepository walletLogRepository
    ) {
        this.flameDispatcher = flameDispatcher;
        this.networkMessageService = networkMessageService;
        this.messagesService = messagesService;
        this.walletUserCache = walletUserCache;
        this.walletOfferConfig = walletOfferConfig;
        this.walletUserRepository = walletUserRepository;
        this.walletLogRepository = walletLogRepository;
    }

    @Execute
    void execute(@Context Player player, @Context WalletUser walletUser) {
        openGui(player, walletUser);

    }

    void openGui(Player player, WalletUser walletUser) {
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 3f, 1f);
        Gui gui = Gui.gui()
                .title(TextUtil.parse(this.messagesService.getMessage("wallet.gui.title")))
                .rows(6)
                .disableAllInteractions()
                .create();

        GuiHelper.fillGui6(gui);

        for (WalletOffer walletOffer : this.walletOfferConfig.getWalletItems()) {

            int size = walletOffer.getVariants().size();
            gui.setItem(walletOffer.getSlot(), FlameItemBuilder.of(walletOffer.getIcon())
                    .glow()
                    .name(walletOffer.getOffer())
                    .lore(BukkitMessage.from(walletOffer.getLore())
                            .with("price", walletOffer.lowestPriceVariant() == null ? "&cBrak wariantów." : walletOffer.lowestPriceVariant().getCost())
                            .with("variants_size", size)
                            .with("user_money", RoundUtil.round(walletUser.getMoney().doubleValue(), 2))
                            .apply())
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
                .title(TextUtil.parse(this.messagesService.getMessage("wallet.selection.gui.title")))
                .disableAllInteractions()
                .create();

        gui.getFiller().fillBorder(FlameItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).name(" ").asGuiItem());

        for (WalletOfferVariant variant : walletOffer.getVariants()) {
            gui.addItem(FlameItemBuilder.of(walletOffer.getIcon())
                    .glow()
                    .name(variant.getName())
                    .amount(variant.getAmount())
                    .lore(TextBuilder.builder().text(variant.getLore())
                            .placeholder("{PRICE}", variant.getCost())
                            .placeholder("{AMOUNT}", variant.getAmount())
                            .build())
                    .asGuiItem(event -> openBuyConfirmationGui(player, walletUser, walletOffer, variant)));
        }

        gui.open(player);

    }

    void openBuyConfirmationGui(Player player, WalletUser walletUser, WalletOffer offer, WalletOfferVariant variant) {
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 3f, 1f);
        Gui gui = Gui.gui()
                .rows(3)
                .title(TextUtil.parse(this.messagesService.getMessage("wallet.confirmation.gui.title")))
                .disableAllInteractions()
                .create();

        gui.getFiller().fill(FlameItemBuilder.of(Material.BLACK_STAINED_GLASS_PANE).name(" ").asGuiItem());
        gui.setItem(List.of(0, 1, 2, 9, 10, 11, 18, 19, 20), FlameItemBuilder.of(Material.GREEN_STAINED_GLASS_PANE)
                .name("&aPotwierdź kupno")
                .asGuiItem(event -> {

                    if (!walletUser.hasEnough(BigDecimal.valueOf(variant.getCost()))) {
                        this.messagesService.getAsText("wallet.not.enough.money")
                                .placeholder("{USER_MONEY}", RoundUtil.round(walletUser.getMoney().doubleValue(), 2))
                                .placeholder("{MONEY_NEEDED}", RoundUtil.round(variant.getCost() - walletUser.getMoney().doubleValue(), 2))
                                .send(player);
                        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                        return;
                    }

                    walletUser.subtractMoney(BigDecimal.valueOf(variant.getCost()));
                    this.flameDispatcher.dispatchAsync(() -> this.walletUserRepository.save(walletUser));
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PlaceholderAPI.setPlaceholders(player, variant.getCommand()
                            .replace("{PLAYER}", player.getName())
                            .replace("{AMOUNT}", String.valueOf(variant.getAmount()))));
                    this.networkMessageService.send(
                            PlaceholderAPI.setPlaceholders(player, TextBuilder.builder()
                                    .text(variant.getBroadcast())
                                    .placeholder("{PLAYER}", player.getName())
                                    .placeholder("{AMOUNT}", variant.getAmount())
                                    .build()),
                            NetworkMessageType.CHAT
                    );

                    WalletLog walletLog = new WalletLog(WalletLogAction.BUY);
                    walletLog.setBuyerName(walletUser.getName());
                    walletLog.setBoughtItem(variant.getName()  + ":" + variant.getAmount());
                    walletLog.setAmount(variant.getCost());
                    this.walletLogRepository.save(walletLog);

                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 3f, 1f);
                    gui.close(player);

                }));

        gui.setItem(List.of(6, 7, 8, 15, 16, 17, 24, 25, 26), FlameItemBuilder.of(Material.RED_STAINED_GLASS_PANE)
                .name("&cAnuluj kupno")
                .asGuiItem(event -> gui.close(player)));

        gui.setItem(2, 5, FlameItemBuilder.of(offer.getIcon())
                .name(variant.getName())
                .amount(variant.getAmount())
                .lore(TextBuilder.builder().text(variant.getLore())
                        .placeholder("{PRICE}", variant.getCost())
                        .placeholder("{AMOUNT}", variant.getAmount())
                        .build())
                .asGuiItem());


        gui.open(player);

    }

}
