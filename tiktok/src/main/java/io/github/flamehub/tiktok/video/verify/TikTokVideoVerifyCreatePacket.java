package io.github.flamehub.tiktok.video.verify;

import io.github.flamehub.commons.messenger.packet.Packet;

public final class TikTokVideoVerifyCreatePacket implements Packet {

  private TikTokVideoVerify tikTokVideoVerify;

  public TikTokVideoVerifyCreatePacket() {
  }

  public TikTokVideoVerifyCreatePacket(final TikTokVideoVerify tikTokVideoVerify) {
    this.tikTokVideoVerify = tikTokVideoVerify;
  }

  public TikTokVideoVerify getTikTokVideoVerify() {
    return tikTokVideoVerify;
  }
}
