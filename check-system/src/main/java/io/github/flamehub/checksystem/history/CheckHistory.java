package io.github.flamehub.checksystem.history;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity("check_history")
public final class CheckHistory implements Serializable {

  @Id
  private final UUID id;
  private CheckHistoryEnding type = CheckHistoryEnding.UNFINISHED;

  private final UUID adminUUID;
  @Indexed
  private final String adminNickname;

  private final UUID checkedPlayerUUID;
  @Indexed
  private final String checkedPlayerNickname;

  private final Instant startTime = Instant.now();
  private Instant endTime;

  private List<String> checkedPlayerChatHistory;

  public CheckHistory(UUID id, UUID adminUUID, String adminNickname, UUID checkedPlayerUUID,
      String checkedPlayerNickname) {
    this.id = id;
    this.adminUUID = adminUUID;
    this.adminNickname = adminNickname;
    this.checkedPlayerUUID = checkedPlayerUUID;
    this.checkedPlayerNickname = checkedPlayerNickname;
  }

  public UUID getId() {
    return id;
  }

  public CheckHistoryEnding getType() {
    return type;
  }

  public void setType(CheckHistoryEnding type) {
    this.type = type;
  }

  public UUID getAdminUUID() {
    return adminUUID;
  }

  public String getAdminNickname() {
    return adminNickname;
  }

  public UUID getCheckedPlayerUUID() {
    return checkedPlayerUUID;
  }


  public String getCheckedPlayerNickname() {
    return checkedPlayerNickname;
  }

  public Instant getStartTime() {
    return startTime;
  }

  public Instant getEndTime() {
    return endTime;
  }

  public void setEndTime(Instant endTime) {
    this.endTime = endTime;
  }

  public List<String> getCheckedPlayerChatHistory() {
    if (checkedPlayerChatHistory == null) {
      checkedPlayerChatHistory = new ArrayList<>();
    }
    return checkedPlayerChatHistory;
  }
}
