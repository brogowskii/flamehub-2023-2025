package io.github.flamehub.daily.reward.user;

import dev.morphia.annotations.Entity;
import io.github.flamehub.commons.user.User;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity("daily_reward_users")
public final class DailyRewardUser extends User {

  private final List<Integer> claimed = new ArrayList<>();

  private Instant nextClaimTime = Instant.now();
  private int currentStreak;

  public DailyRewardUser() {
  }

  public DailyRewardUser(UUID uniqueId, String name) {
    super(uniqueId, name);
  }

  public Instant getNextClaimTime() {
    return nextClaimTime;
  }

  public void setNextClaimTime(Instant nextClaimTime) {
    this.nextClaimTime = nextClaimTime;
  }

  public int getCurrentStreak() {
    return currentStreak;
  }

  public void setCurrentStreak(int currentStreak) {
    this.currentStreak = currentStreak;
  }

  public List<Integer> getClaimed() {
    return claimed;
  }
}
