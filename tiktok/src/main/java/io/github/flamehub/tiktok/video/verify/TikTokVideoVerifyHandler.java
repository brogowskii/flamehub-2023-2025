package io.github.flamehub.tiktok.video.verify;

import io.github.flamehub.commons.messenger.packet.PacketHandler;

public final class TikTokVideoVerifyHandler {

  private final TikTokVideoVerifyCache tikTokVideoVerifyCache;

  public TikTokVideoVerifyHandler(final TikTokVideoVerifyCache tikTokVideoVerifyCache) {
    this.tikTokVideoVerifyCache = tikTokVideoVerifyCache;
  }

  @PacketHandler
  public void handle(final TikTokVideoVerifyCreatePacket packet) {
    final TikTokVideoVerify tikTokVideoVerify = packet.getTikTokVideoVerify();
    tikTokVideoVerifyCache.add(tikTokVideoVerify.getId(), tikTokVideoVerify);
  }

  @PacketHandler
  public void handle(final TikTokVideoVerifyRemovePacket packet) {
    tikTokVideoVerifyCache.remove(packet.getId());
  }

  @PacketHandler
  public void handle(final TikTokVideoVerifyStatusPacket packet) {
    final TikTokVideoVerify tikTokVideoVerify = tikTokVideoVerifyCache.findByKey(packet.getId());
    if (tikTokVideoVerify == null) {
      return;
    }
    tikTokVideoVerify.setStatus(packet.getStatus());
  }

}
