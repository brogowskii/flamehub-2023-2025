package io.github.flamehub.reward.api;

import com.google.gson.annotations.SerializedName;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;

@Entity("discord_rewards")
public final class RewardReceivedEntry {

    @Id
    private String playerName;

    @Indexed
    private long userId;

    @Indexed
    private String serverCategory;

    public RewardReceivedEntry() {

    }

    public RewardReceivedEntry(String playerName, long userId, String serverCategory) {
        this.playerName = playerName;
        this.userId = userId;
        this.serverCategory = serverCategory;
    }

    public String getPlayerName() {
        return playerName;
    }

    public long getUserId() {
        return userId;
    }

    public String getServerCategory() {
        return serverCategory;
    }
}
