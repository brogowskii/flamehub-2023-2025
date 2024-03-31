package io.github.flamehub.daily.reward;

import java.io.Serializable;
import java.util.List;

public final class DailyReward implements Serializable {

    private int streak;

    private List<String> command;
    private List<String> rewards;

    public DailyReward() {
    }

    public DailyReward(int streak, List<String> command, List<String> rewards) {
        this.streak = streak;
        this.command = command;
        this.rewards = rewards;
    }

    public List<String> getCommand() {
        return command;
    }

    public List<String> getRewards() {
        return rewards;
    }

    public int getStreak() {
        return streak;
    }
}
