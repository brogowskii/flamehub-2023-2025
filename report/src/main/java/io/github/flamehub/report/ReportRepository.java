package io.github.flamehub.report;

import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;
import io.github.flamehub.commons.database.DatabaseRepository;

public final class ReportRepository extends DatabaseRepository<Report> {

  public ReportRepository(final Datastore datastore) {
    super(datastore, Report.class);
  }

  public Report findByReporterAndTarget(final String reporter, final String target) {
    return datastore.find(entityClass)
        .filter(Filters.eq("reporter", reporter))
        .filter(Filters.eq("targetName", target))
        .first();
  }
}
