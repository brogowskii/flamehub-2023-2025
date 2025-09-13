package io.github.flamehub.commons.punishment;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;
import java.time.Instant;
import java.util.UUID;

@Entity("punishments")
public final class Punishment {

  @Id
  private UUID uniqueId;

  @Indexed
  private PunishmentType type;
  private Instant creationTime;

  @Indexed
  private String punished;

  @Indexed
  private String punishedIp;

  private String reason;
  private String admin;
  private Instant expireTime;

  public Punishment() {
  }

  public Punishment(final PunishmentType type, final String punished, final String reason, final String admin,
      final Instant expireTime) {
    uniqueId = UUID.randomUUID();
    this.type = type;
    this.punished = punished;
    this.reason = reason;
    this.admin = admin;
    this.expireTime = expireTime;
    creationTime = Instant.now();
  }

  public UUID getUniqueId() {
    return uniqueId;
  }

  public PunishmentType getType() {
    return type;
  }

  public String getPunished() {
    return punished;
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

  public Instant getExpireTime() {
    return expireTime;
  }

  public void setExpireTime(final Instant expireTime) {
    this.expireTime = expireTime;
  }

  public boolean isExpired() {
    if (expireTime == null) {
      return false;
    }
    return expireTime.isBefore(Instant.now());
  }

  public String getPunishedIp() {
    return punishedIp;
  }

  public void setPunishedIp(final String punishedIp) {
    this.punishedIp = punishedIp;
  }

  public Instant getCreationTime() {
    return creationTime;
  }
}
