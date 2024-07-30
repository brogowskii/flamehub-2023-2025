package io.github.flamehub.restapi.wallet;

import io.github.flamehub.commons.database.DatabaseConnector;
import io.github.flamehub.commons.database.DatastoreFactory;
import io.github.flamehub.wallet.api.WalletUser;
import io.github.flamehub.wallet.api.WalletUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class WalletConfiguration {

  @Bean
  WalletUserRepository walletUserRepository(final DatabaseConnector databaseConnector) {
    return new WalletUserRepository(
        DatastoreFactory.create(databaseConnector.getMongoClient(), "global", WalletUser.class),
        WalletUser.class);
  }


}
