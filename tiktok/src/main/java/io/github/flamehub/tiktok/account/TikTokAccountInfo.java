package io.github.flamehub.tiktok.account;

public final class TikTokAccountInfo {

  private final String secUid;
  private final String uniqueId;
  private final String nickname;
  private final String signature;

  public TikTokAccountInfo(final String secUid, final String uniqueId, final String nickname,
      final String signature) {
    this.secUid = secUid;
    this.uniqueId = uniqueId;
    this.nickname = nickname;
    this.signature = signature;
  }

  public String getSecUid() {
    return secUid;
  }

  public String getUniqueId() {
    return uniqueId;
  }

  public String getNickname() {
    return nickname;
  }

  public String getSignature() {
    return signature;
  }

  @Override
  public String toString() {
    return "TikTokAccountInfo{" +
        "secUid='" + secUid + '\'' +
        ", uniqueId='" + uniqueId + '\'' +
        ", nickname='" + nickname + '\'' +
        ", signature='" + signature + '\'' +
        '}';
  }
}
