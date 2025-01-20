package io.github.flamehub.proxy.core.blacklist;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import io.github.flamehub.commons.util.TimeUtil;
import io.github.flamehub.proxy.core.ProxyMessages;
import io.github.flamehub.proxy.core.util.TextUtil;

public final class BlacklistListener {

  private final BlacklistRepository blacklistRepository;
  private final ProxyMessages proxyMessages;

  public BlacklistListener(
      final BlacklistRepository blacklistRepository,
      final ProxyMessages proxyMessages) {
    this.blacklistRepository = blacklistRepository;
    this.proxyMessages = proxyMessages;
  }

  @Subscribe(priority = Short.MAX_VALUE)
  public void onBlacklist(final PreLoginEvent event) {
    final String username = event.getUsername();
    final Blacklist blacklist = blacklistRepository.load("nickname", username);
    if (blacklist != null) {
      event.setResult(TextUtil.preDenied(proxyMessages.blacklistKick
          .with("reason", blacklist.getReason())
          .with("admin", blacklist.getAdmin())
          .with("date", TimeUtil.formatDate(blacklist.getDate()))
          .applyFirstAsComponent()));
    }
  }

}
