package io.github.flamehub.achievements.achievement;

import io.github.flamehub.achievements.achievement.user.AchievementUser;
import java.util.ArrayList;
import java.util.List;

public final class AchievementService {

  private final AchievementConfig achievementConfig;

  public AchievementService(final AchievementConfig achievementConfig) {
    this.achievementConfig = achievementConfig;
  }

  public List<Achievement> findByCategories(final String category) {
    final List<Achievement> achievements = new ArrayList<>();
    for (final List<Achievement> values : achievementConfig.getAchievementsByCategory().values()) {
      for (final Achievement value : values) {
        if (category.equals(value.getCategory())) {
          achievements.add(value);
        }
      }
    }
    return achievements;
  }

  public List<AchievementCategory> getAchievementsCategoryByAction(
      final AchievementActionType actionType) {
    final List<AchievementCategory> categories = new ArrayList<>();
    for (final AchievementCategory category : achievementConfig.getAchievementCategories()
        .values()) {
      if (category.getAction().getActionType().equals(actionType)) {
        categories.add(category);
      }
    }
    return categories;
  }

  public int availableToClaimFromCategory(final String category, final AchievementUser user) {
    int count = 0;
    for (final List<Achievement> values : achievementConfig.getAchievementsByCategory().values()) {
      for (final Achievement value : values) {
        if (value.getCategory().equals(category)) {
          if (user.getAchievementProgress(value.getCategory()) >= value.getRequired() &&
              !user.isAchievementClaimed(value)) {
            count++;
          }
        }
      }
    }
    return count;
  }

  public int size(final String category) {
    final List<Achievement> achievements = achievementConfig.getAchievementsByCategory()
        .get(category);
    return achievements != null ? achievements.size() : 0;
  }
}