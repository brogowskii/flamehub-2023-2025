import com.google.gson.Gson;
import io.github.flamehub.commons.config.FlameConfigService;
import io.github.flamehub.commons.config.RemoteRepository;
import io.github.flamehub.commons.config.RemoteUpdateHandler;
import io.github.flamehub.commons.config.serializer.FlameGsonConfigSerializer;
import io.github.flamehub.commons.database.DatabaseConnector;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.property.PropertyLoader;
import io.github.flamehub.commons.redis.RedisService;

import java.io.File;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class Test {

    public static void main(String[] args) throws IllegalAccessException {

        DatabaseConnector databaseConnector = new DatabaseConnector(
                "mongodb://admin:FGZtTvywHqQ5MWY9t2KhahqSh8ACVFpRH9TnmVyhSuxSBBTxPH@85.10.196.21:10001"
        );
        RedisService redisService = new RedisService(
                "85.10.196.21",
                "hBCrmERhtnh3JQd94rF935E4Pn2mXC8Jt8bgeGa2fmTWAw8qwa",
                10000
        );
        RedisMessenger redisMessenger = new RedisMessenger(redisService.getClient());

        FlameGsonConfigSerializer serializer = new FlameGsonConfigSerializer(new Gson());
        RemoteRepository remoteRepository = new RemoteRepository(serializer, databaseConnector.getMongoClient(), "test_cfg");
        FlameConfigService flameConfigService = new FlameConfigService(redisMessenger, remoteRepository, serializer);

        TestConfig testConfig = flameConfigService.getOrCreate(
                new File("/Users/kornelbrogowski/IdeaProjects/flamehub-recode/commons/src/main/resources"),
                TestConfig.class
        );

        CompletableFuture.runAsync(() -> {
            redisMessenger.subscribe(FlameConfigService.REMOTE_CONFIG_UPDATE_CHANNEL, new RemoteUpdateHandler(flameConfigService));
        });

        Scanner scanner = new Scanner(System.in);
        while (true) {

            System.out.println("1. Wyświetl zawartosc");
            System.out.println("2. save");
            System.out.println("3. refresh");
            System.out.println("4. wyswietl credentials.properties");

            String input = scanner.nextLine();
            switch (input) {
                case "1" -> System.out.println(testConfig.getTest());
                case "2" -> flameConfigService.update(TestConfig.class);
                case "3" -> flameConfigService.refresh(TestConfig.class, false);
                case "4" -> System.out.println(new PropertyLoader("credentials.properties").getProperty("mongo.uri"));
                default -> System.out.println("Nieznana komenda");
            }

        }


    }

}
