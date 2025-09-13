package io.github.flamehub.cheque;

public final class ChequeLogBuilder {

  private ChequeLogType type;
  private String who;
  private double money;

  public ChequeLogBuilder type(final ChequeLogType type) {
    this.type = type;
    return this;
  }

  public ChequeLogBuilder who(final String who) {
    this.who = who;
    return this;
  }

  public ChequeLogBuilder money(final double money) {
    this.money = money;
    return this;
  }


  public ChequeLog build() {
    return new ChequeLog(type, who, money);
  }

}
