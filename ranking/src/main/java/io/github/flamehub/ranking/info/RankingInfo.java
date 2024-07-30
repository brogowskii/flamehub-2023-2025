package io.github.flamehub.ranking.info;

import io.github.flamehub.ranking.RankingItem;
import java.io.Serializable;

public final class RankingInfo implements Serializable {

  private String id;
  private String guiId;
  private RankingItem item;

  private String collection;
  private String database;
  private String field;
  private String entryField;
  private int limit;

  public RankingInfo() {
  }

  public RankingInfo(String id, String guiId, RankingItem item, String collection, String database,
      String field, String entryField, int limit) {
    this.id = id;
    this.guiId = guiId;
    this.item = item;
    this.collection = collection;
    this.database = database;
    this.field = field;
    this.entryField = entryField;
    this.limit = limit;
  }

  public String getId() {
    return id;
  }

  public String getGuiId() {
    return guiId;
  }

  public RankingItem getItem() {
    return item;
  }

  public String getCollection() {
    return collection;
  }

  public String getDatabase() {
    return database;
  }

  public String getField() {
    return field;
  }

  public String getEntryField() {
    return entryField;
  }

  public int getLimit() {
    return limit;
  }
}
