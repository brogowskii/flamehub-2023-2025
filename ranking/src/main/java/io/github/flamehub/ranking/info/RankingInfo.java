package io.github.flamehub.ranking.info;

import io.github.flamehub.ranking.RankingItem;
import java.io.Serializable;
import java.util.List;

public final class RankingInfo implements Serializable {

  private String id;
  private String guiId;
  private RankingItem item;

  private String collection;
  private String database;
  private List<String> field;
  private String entryField;
  private int limit;

  public RankingInfo() {
  }

  public RankingInfo(
      final String id,
      final String guiId,
      final RankingItem item,
      final String collection,
      final String database,
      final List<String> field,
      final String entryField,
      final int limit
  ) {
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

  public List<String> getField() {
    return field;
  }

  public String getEntryField() {
    return entryField;
  }

  public int getLimit() {
    return limit;
  }
}
