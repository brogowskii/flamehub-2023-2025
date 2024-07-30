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

  public VPNEntry(String ipAddress, boolean block) {
    this.ipAddress = ipAddress;
    this.block = block;
    renewExpiration();
  }

  public void renewExpiration() {
    this.expiration = Instant.now().plus(24, ChronoUnit.HOURS);
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public boolean isBlock() {
    return block;
  }

  public void setBlock(boolean block) {
    this.block = block;
  }

  public Instant getExpiration() {
    return expiration;
  }

  public void setExpiration(Instant expiration) {
    this.expiration = expiration;
  }
}
