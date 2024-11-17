package io.github.flamehub.achievements.achievement;

import io.github.flamehub.achievements.achievement.user.AchievementUser;
import java.util.ArrayList;
import java.util.List;

public final class AchievementService {

  private AchievementConfig achievementConfig;

  public AchievementService(AchievementConfig achievementConfig) {
    this.achievementConfig = achievementConfig;
  }

  public List<Achievement> findByCategories(String category) {
    List<Achievement> achievements = new ArrayList<>();
    for (List<Achievement> values : this.achievementConfig.getAchievementsByCategory().values()) {
      for (Achievement value : values) {
        if (category.equals(value.getCategory())) {
          achievements.add(value);
        }
      }
    }
    return achievements;
  }

  public List<AchievementCategory> getAchievementsCategoryByAction(
      AchievementActionType actionType) {
    List<AchievementCategory> categories = new ArrayList<>();
    for (AchievementCategory category : this.achievementConfig.getAchievementCategories()
        .values()) {
      if (category.getAction().getActionType().equals(actionType)) {
        categories.add(category);
      }
    }
    return categories;
  }

  public int availableToClaimFromCategory(String category, AchievementUser user) {
    int count = 0;
    for (List<Achievement> values : this.achievementConfig.getAchievementsByCategory().values()) {
      for (Achievement value : values) {
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

  public int size(String category) {
    List<Achievement> achievements = this.achievementConfig.getAchievementsByCategory()
        .get(category);
    return achievements != null ? achievements.size() : 0;
  }
}