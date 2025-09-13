package io.github.flamehub.proxy.core.vpn;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Entity("vpn_entries")
public final class VPNEntry {

  @Id
  private final String ipAddress;
  private boolean block;
  private Instant expiration;

  public VPNEntry(final String ipAddress, final boolean block) {
    this.ipAddress = ipAddress;
    this.block = block;
    renewExpiration();
  }

  public void renewExpiration() {
    expiration = Instant.now().plus(24, ChronoUnit.HOURS);
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public boolean isBlock() {
    return block;
  }

  public void setBlock(final boolean block) {
    this.block = block;
  }

  public Instant getExpiration() {
    return expiration;
  }

  public void setExpiration(final Instant expiration) {
    this.expiration = expiration;
  }
}
