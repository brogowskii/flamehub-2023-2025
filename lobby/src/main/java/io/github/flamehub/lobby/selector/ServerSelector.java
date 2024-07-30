package io.github.flamehub.lobby.selector;

import java.io.Serializable;

public final class ServerSelector implements Serializable {

  private String category;
  private String infoFrom;
  private String joinCommand;
  private String startDate;
  private ServerSelectorItem item;

  public ServerSelector(String category, String infoFrom, String joinCommand, String startDate,
      ServerSelectorItem item) {
    this.category = category;
    this.infoFrom = infoFrom;
    this.joinCommand = joinCommand;
    this.startDate = startDate;
    this.item = item;
  }

  public ServerSelector() {
  }

  public String getCategory() {
    return category;
  }

  public String getInfoFrom() {
    return infoFrom;
  }

  public String getJoinCommand() {
    return joinCommand;
  }

  public String getStartDate() {
    return startDate;
  }

  public ServerSelectorItem getItem() {
    return item;
  }
}
