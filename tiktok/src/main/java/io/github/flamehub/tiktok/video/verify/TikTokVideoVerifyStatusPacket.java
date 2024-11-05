package io.github.flamehub.tiktok.video.verify;

import io.github.flamehub.commons.messenger.packet.Packet;

public final class TikTokVideoVerifyStatusPacket implements Packet {

  private String id;
  private TikTokVideoVerifyStatus status;

  public TikTokVideoVerifyStatusPacket() {
  }

  public TikTokVideoVerifyStatusPacket(final String id, final TikTokVideoVerifyStatus status) {
    this.id = id;
    this.status = status;
  }

  public String getId() {
    return id;
  }

  public TikTokVideoVerifyStatus getStatus() {
    return status;
  }


}
