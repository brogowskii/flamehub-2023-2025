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

    public Punishment(PunishmentType type, String punished, String reason, String admin, Instant expireTime) {
        this.uniqueId = UUID.randomUUID();
        this.type = type;
        this.punished = punished;
        this.reason = reason;
        this.admin = admin;
        this.expireTime = expireTime;
        this.creationTime = Instant.now();
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

    public String getAdmin() {
        return admin;
    }

    public Instant getExpireTime() {
        return expireTime;
    }

    public boolean isExpired() {
        return this.expireTime.isBefore(Instant.now());
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public void setExpireTime(Instant expireTime) {
        this.expireTime = expireTime;
    }

    public String getPunishedIp() {
        return punishedIp;
    }

    public void setPunishedIp(String punishedIp) {
        this.punishedIp = punishedIp;
    }

    public Instant getCreationTime() {
        return creationTime;
    }
}
