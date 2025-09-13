package io.github.flamehub.commons.bukkit.nametag;

public final class NameTagTeam {

  private final String teamName;
  private String prefix;
  private String suffix;

  public NameTagTeam(final String teamName) {
    this.teamName = teamName;
  }

  public String getTeamName() {
    return teamName;
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(final String prefix) {
    this.prefix = prefix;
  }

  public String getSuffix() {
    return suffix;
  }

  public void setSuffix(final String suffix) {
    this.suffix = suffix;
  }
}
