package io.github.flamehub.report;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import java.util.Date;
import java.util.UUID;

@Entity("reports")
public final class Report {

  @Id
  private UUID uuid = UUID.randomUUID();

  private ReportType type;

  private Date createDate;

  private String targetName;
  private UUID targetUuid;

  private String reporter;

  public Report(
      final ReportType type,
      final String targetName,
      final UUID targetUuid,
      final String reporter
  ) {
    this.type = type;
    createDate = new Date();
    this.targetName = targetName;
    this.targetUuid = targetUuid;
    this.reporter = reporter;
  }

  public UUID getUuid() {
    return uuid;
  }

  public ReportType getType() {
    return type;
  }

  public Date getCreateDate() {
    return createDate;
  }

  public String getTargetName() {
    return targetName;
  }

  public UUID getTargetUuid() {
    return targetUuid;
  }

  public String getReporter() {
    return reporter;
  }
}
