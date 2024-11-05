package io.github.flamehub.tiktok.video.verify;

import io.github.flamehub.commons.messenger.packet.Packet;

public final class TikTokVideoVerifyRemovePacket implements Packet {

  private String id;


  public TikTokVideoVerifyRemovePacket() {
  }

  public TikTokVideoVerifyRemovePacket(final String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }
}
