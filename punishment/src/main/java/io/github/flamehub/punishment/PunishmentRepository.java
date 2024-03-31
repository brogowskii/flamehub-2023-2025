package io.github.flamehub.punishment;

import dev.morphia.Datastore;
import dev.morphia.query.Query;
import dev.morphia.query.filters.Filters;
import io.github.flamehub.commons.database.DatabaseRepository;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

public final class PunishmentRepository extends DatabaseRepository<Punishment> {

    public PunishmentRepository(Datastore datastore, Class<Punishment> entityClass) {
        super(datastore, entityClass);
    }

    public Punishment load(String punished, String category, PunishmentType type) {
        Pattern pattern = Pattern.compile("^(?i)" + Pattern.quote(punished) + "$");

        Query<Punishment> query = this.datastore.find(Punishment.class)
                .filter(Filters.regex("punished", pattern))
                .filter(Filters.eq("serverCategory", category))
                .filter(Filters.eq("type", type.toString()));

        return query.first();
    }

    public Punishment loadByIp(String punishedIp, String category, PunishmentType type) {
        return this.datastore.find(Punishment.class)
                .filter(
                        Filters.eq("punishedIp", punishedIp),
                        Filters.eq("serverCategory", category),
                        Filters.eq("type", type.toString())
                )
                .first();
    }

    public Punishment isBanned(Player player, String ip, String category) {
        Punishment punishment = this.loadByIp(ip, category, PunishmentType.BAN_IP);
        if (punishment == null) {
            punishment = this.load(player.getName(), category, PunishmentType.BAN_IP);
        }
        if (punishment != null) {
            punishment.setPunishedIp(ip);
        }
        else {
            punishment = this.load(player.getName(), category, PunishmentType.BAN);
        }

        if (punishment != null) {
            if (punishment.getExpireTime() != null && punishment.isExpired()) {
                this.delete(punishment);
                return null;
            }

            return punishment;
        }

        return null;
    }




}
