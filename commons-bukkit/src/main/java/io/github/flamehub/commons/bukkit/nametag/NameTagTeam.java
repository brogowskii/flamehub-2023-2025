package io.github.flamehub.commons.bukkit.nametag;

public class NameTagTeam {

    private final String teamName;
    private String prefix;
    private String suffix;

    public NameTagTeam(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
