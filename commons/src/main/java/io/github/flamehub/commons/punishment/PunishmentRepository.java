package io.github.flamehub.commons.punishment;

import dev.morphia.Datastore;
import dev.morphia.query.Query;
import dev.morphia.query.filters.Filters;
import io.github.flamehub.commons.database.DatabaseRepository;
import java.util.regex.Pattern;

public final class PunishmentRepository extends DatabaseRepository<Punishment> {

  public PunishmentRepository(Datastore datastore) {
    super(datastore, Punishment.class);
  }

  public Punishment load(String punished, PunishmentType type) {
    Pattern pattern = Pattern.compile("^(?i)" + Pattern.quote(punished) + "$");

    Query<Punishment> query = datastore.find(Punishment.class)
        .filter(Filters.regex("punished", pattern))
        .filter(Filters.eq("type", type.toString()));

    return query.first();
  }

  public Punishment loadByIp(String punishedIp, PunishmentType type) {
    return datastore.find(Punishment.class)
        .filter(
            Filters.eq("punishedIp", punishedIp),
            Filters.eq("type", type.toString())
        )
        .first();
  }

  public Punishment isBanned(String playerName, String ip) {
    Punishment punishment = loadByIp(ip, PunishmentType.BAN_IP);
    if (punishment == null) {
      punishment = load(playerName, PunishmentType.BAN_IP);
    }
    if (punishment != null) {
      punishment.setPunishedIp(ip);
    } else {
      punishment = load(playerName, PunishmentType.BAN);
    }

    if (punishment != null) {
      if (punishment.getExpireTime() != null && punishment.isExpired()) {
        delete(punishment);
        return null;
      }

      return punishment;
    }

    return null;
  }

}
