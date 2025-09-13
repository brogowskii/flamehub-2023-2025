package io.github.flamehub.cheque;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import java.util.Date;
import java.util.UUID;

@Entity("cheque_logs")
public final class ChequeLog {

  @Id
  private final UUID id = UUID.randomUUID();
  private ChequeLogType type;

  private String who;
  private double money;
  private final Date date = new Date();

  public ChequeLog() {
  }

  public ChequeLog(final ChequeLogType type, final String who, final double money) {
    this.type = type;
    this.who = who;
    this.money = money;
  }

  public static ChequeLogBuilder builder() {
    return new ChequeLogBuilder();
  }

  public UUID getId() {
    return id;
  }

  public ChequeLogType getType() {
    return type;
  }

  public String getWho() {
    return who;
  }

  public double getMoney() {
    return money;
  }

  public Date getDate() {
    return date;
  }
}
