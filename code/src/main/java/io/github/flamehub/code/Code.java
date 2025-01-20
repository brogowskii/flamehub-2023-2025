package io.github.flamehub.code;

import java.io.Serializable;
import java.util.List;

final class Code implements Serializable {

  private String name;
  private List<String> commands;
  private List<String> broadcast;
  private String requiredTime;

  Code() {
  }

  Code(String name, List<String> commands, List<String> broadcast, String requiredTime) {
    this.name = name;
    this.commands = commands;
    this.broadcast = broadcast;
    this.requiredTime = requiredTime;
  }

  public String getName() {
    return name;
  }

  public List<String> getCommands() {
    return commands;
  }

  public String getRequiredTime() {
    return requiredTime;
  }

  public List<String> getBroadcast() {
    return broadcast;
  }
}
