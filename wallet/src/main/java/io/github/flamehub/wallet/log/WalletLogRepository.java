package io.github.flamehub.wallet.log;

import dev.morphia.Datastore;
import dev.morphia.query.Query;
import dev.morphia.query.filters.Filters;
import io.github.flamehub.commons.database.DatabaseRepository;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public final class WalletLogRepository extends DatabaseRepository<WalletLog> {

  public WalletLogRepository(Datastore datastore, Class<WalletLog> entityClass) {
    super(datastore, entityClass);
  }

  public List<WalletLog> load(String who, WalletLogAction action) {
    Pattern pattern = Pattern.compile("^(?i)" + Pattern.quote(who) + "$");

    Query<WalletLog> query = this.datastore.find(WalletLog.class)
        .filter(Filters.regex(
            action == WalletLogAction.ADD_MONEY || action == WalletLogAction.REMOVE_MONEY
                ? "adminName" : "buyerName", pattern))
        .filter(Filters.eq("action", action.toString()));

    Iterator<WalletLog> iterator = query.iterator();
    List<WalletLog> walletLogs = new ArrayList<>();
    while (iterator.hasNext()) {
      walletLogs.add(iterator.next());
    }
    return walletLogs;
  }

}
