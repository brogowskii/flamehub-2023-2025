package io.github.flamehub.restapi;

import io.github.flamehub.commons.database.DatabaseConnector;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.redis.RedisService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class RestApiConfiguration {

  @Bean
  RedisService redisService() {
    return new RedisService("85.10.196.21", "hBCrmERhtnh3JQd94rF935E4Pn2mXC8Jt8bgeGa2fmTWAw8qwa",
        10000);
  }

  @Bean
  RedisMessenger redisMessenger(final RedisService redisService) {
    return new RedisMessenger(redisService.getClient());
  }

  @Bean
  DatabaseConnector databaseConnector() {
    return new DatabaseConnector(
        "mongodb://admin:FGZtTvywHqQ5MWY9t2KhahqSh8ACVFpRH9TnmVyhSuxSBBTxPH@85.10.196.21:10001");
  }

}
