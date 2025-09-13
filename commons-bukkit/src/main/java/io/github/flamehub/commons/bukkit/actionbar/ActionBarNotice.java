package io.github.flamehub.commons.bukkit.actionbar;

import java.util.function.Predicate;
import org.bukkit.entity.Player;

public class ActionBarNotice {

  private final String type;
  private final int priority;
  private long expireTime;
  private String text;
  private Predicate<Player> condition;

  public ActionBarNotice(
      final String type,
      final int priority,
      final String text,
      final long expireTime,
      final Predicate<Player> condition) {
    this.type = type;
    this.priority = priority;
    this.text = text;
    this.expireTime = expireTime;
    this.condition = condition;
  }

  public static ActionBarNoticeBuilder builder() {
    return new ActionBarNoticeBuilder();
  }

  public String getType() {
    return type;
  }

  public int getPriority() {
    return priority;
  }

  public String getText() {
    return text;
  }

  public void setText(final String text) {
    this.text = text;
  }

  public long getExpireTime() {
    return expireTime;
  }

  public void setExpireTime(final long expireTime) {
    this.expireTime = expireTime;
  }

  public Predicate<Player> getCondition() {
    return condition;
  }

  public void setCondition(final Predicate<Player> condition) {
    this.condition = condition;
  }
}