package io.github.flamehub.wallet;

import io.github.flamehub.wallet.user.WalletUser;
import io.github.flamehub.wallet.user.WalletUserFacade;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

final class WalletPlaceholder extends PlaceholderExpansion {

  private final WalletUserFacade walletUserFacade;

  WalletPlaceholder(final WalletUserFacade walletUserFacade) {
    this.walletUserFacade = walletUserFacade;
  }

  @Override
  public @NotNull String getIdentifier() {
    return "wallet";
  }

  @Override
  public @NotNull String getAuthor() {
    return "MarcinOpałka";
  }

  @Override
  public @NotNull String getVersion() {
    return "0.1";
  }

  @Override
  public String onRequest(final OfflinePlayer player, @NotNull final String params) {

    final WalletUser walletUser = walletUserFacade.findByUniqueId(player.getUniqueId());
    if (walletUser == null) {
      return "";
    }

    if ("money".equalsIgnoreCase(params)) {
      return String.valueOf(walletUser.getMoney().doubleValue());
    }

    return "";
  }
}
