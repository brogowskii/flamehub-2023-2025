package io.github.flamehub.proxy.core.blacklist;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import java.time.Instant;

@Entity("blacklist")
public final class Blacklist {

  @Id
  private String nickname;
  private String reason;
  private String admin;
  private Instant date;

  public Blacklist(final String nickname, final String reason, final String admin,
      final Instant date) {
    this.nickname = nickname;
    this.reason = reason;
    this.admin = admin;
    this.date = date;
  }

  public Blacklist() {
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(final String nickname) {
    this.nickname = nickname;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(final String reason) {
    this.reason = reason;
  }

  public String getAdmin() {
    return admin;
  }

  public void setAdmin(final String admin) {
    this.admin = admin;
  }

  public Instant getDate() {
    return date;
  }

  public void setDate(final Instant date) {
    this.date = date;
  }
}
