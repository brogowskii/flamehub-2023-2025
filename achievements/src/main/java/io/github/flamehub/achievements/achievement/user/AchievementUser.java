package io.github.flamehub.achievements.achievement.user;

import dev.morphia.annotations.Entity;
import io.github.flamehub.achievements.achievement.Achievement;
import io.github.flamehub.achievements.achievement.AchievementCategory;
import io.github.flamehub.commons.user.UserUpdatable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Entity("achievement_users")
public final class AchievementUser extends UserUpdatable {

  private final Set<String> claimedAchievements = new HashSet<>();
  private final Map<String, Long> achievementProgress = new HashMap<>();

  public AchievementUser(final UUID uniqueId, final String name) {
    super(uniqueId, name);
  }

  public AchievementUser() {
  }

  public Long getAchievementProgress(String type) {
    return achievementProgress.getOrDefault(type, 0L);
  }

  public void addAchievementProgress(String type, long progress) {
    long currentProgress = achievementProgress.getOrDefault(type, 0L);
    achievementProgress.put(type, currentProgress + progress);
  }

  public void setAchievementProgress(String type, long progress) {
    achievementProgress.put(type, progress);
  }

  public boolean isAchievementClaimed(Achievement achievement) {
    return claimedAchievements.contains(achievement.getCategory() + ":" + achievement.getId());
  }

  public void addClaimedAchievement(Achievement achievement) {
    claimedAchievements.add(achievement.getCategory() + ":" + achievement.getId());
  }

  public Long claimedAchievementsCount(AchievementCategory type) {
    return claimedAchievements.stream()
        .filter(achievement -> {
          String[] split = achievement.split(":");
          String typeFromSplit = split[0];
          return type.getId().equals(typeFromSplit);
        })
        .count();
  }
}