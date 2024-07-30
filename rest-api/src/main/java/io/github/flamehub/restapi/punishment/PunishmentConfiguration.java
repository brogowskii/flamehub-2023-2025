package io.github.flamehub.restapi.punishment;

import io.github.flamehub.commons.database.DatabaseConnector;
import io.github.flamehub.commons.database.DatastoreFactory;
import io.github.flamehub.commons.punishment.Punishment;
import io.github.flamehub.commons.punishment.PunishmentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class PunishmentConfiguration {

  @Bean
  PunishmentRepository punishmentRepository(DatabaseConnector databaseConnector) {
    return new PunishmentRepository(
        DatastoreFactory.create(databaseConnector.getMongoClient(), "global", Punishment.class));
  }

}
