package io.github.flamehub.commons.bukkit.actionbar;

import java.util.function.Predicate;
import org.bukkit.entity.Player;

public class ActionBarNoticeBuilder {

  private String type;
  private int priority;
  private String text;
  private long expireTime;
  private Predicate<Player> condition;

  public ActionBarNoticeBuilder type(final String type) {
    this.type = type;
    return this;
  }

  public ActionBarNoticeBuilder text(final String text) {
    this.text = text;
    return this;
  }

  public ActionBarNoticeBuilder expireTime(final long expireTime) {
    this.expireTime = expireTime;
    return this;
  }

  public ActionBarNoticeBuilder priority(final int priority) {
    this.priority = priority;
    return this;
  }

  public ActionBarNoticeBuilder condition(final Predicate<Player> condition) {
    this.condition = condition;
    return this;
  }

  public ActionBarNotice build() {

    if (expireTime == 0) {
      expireTime = System.currentTimeMillis() + 3000;
    }

    return new ActionBarNotice(type, priority, text, expireTime, condition); // Add condition here
  }
}