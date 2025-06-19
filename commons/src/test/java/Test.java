import io.github.flamehub.commons.database.DatabaseConnector;
import io.github.flamehub.commons.database.DatastoreFactory;
import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.redis.RedisService;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public class Test {

  public static void main(String[] args)
      throws IllegalAccessException, InterruptedException, ClassNotFoundException {

    DatabaseConnector databaseConnector = new DatabaseConnector(
        "mongodb://flameroot:%26Xr%2C%23~sk*c%7B47M5v167%3DkurQmTY%C2%A3t20a@162.55.64.219:27017"
    );
    RedisService redisService = new RedisService(
        "162.55.64.219",
        "rCeLaSyCEalentRoGEtyperMIDEmenth",
        6379,
        Test.class.getClassLoader()
    );
    RedisMessenger redisMessenger = new RedisMessenger(redisService.getClient());
    redisMessenger.subscribe("test", new TestPacketHandler());
    redisMessenger.publish("test", new TestPacket());

//    System.out.println(Serializable.class.getName());
//    Class.forName(Serializable.class.getName());




//    TestUserRepository testUserRepository = new TestUserRepository(
//        DatastoreFactory.create(databaseConnector.getMongoClient(), "test", TestUser.class));
//    TestUserCache testUserCache = new TestUserCache(redisMessenger, redisService,
//        testUserRepository);

//    TestUser testUser = new TestUser(UUID.randomUUID(), "cwells ");
//    testUserRepository.save(testUser);

//    testUserCache.mutate(
//            UUID.fromString("1b6e4899-2f45-450a-be7d-c5b3940bac8e"),
//            mutator -> {
//
//              mutator.setPoints(ThreadLocalRandom.current().nextInt(500, 1500));
//              System.out.println("a");
//
//            });
//

//    FlameGsonConfigSerializer serializer = new FlameGsonConfigSerializer(new Gson());
//    RemoteRepository remoteRepository = new RemoteRepository(serializer,
//        databaseConnector.getMongoClient(), "test_cfg");
//        FlameConfigService flameConfigService = new FlameConfigService(redisMessenger, remoteRepository, serializer);
//
//        TestConfig testConfig = flameConfigService.getOrCreate(
//                new File("/Users/kornelbrogowski/IdeaProjects/flamehub-recode/commons/src/main/resources"),
//                TestConfig.class
//        );
//
//        CompletableFuture.runAsync(() -> {
//            redisMessenger.subscribe(FlameConfigService.REMOTE_CONFIG_UPDATE_CHANNEL, new RemoteUpdateHandler(flameConfigService));
//        });
//
//        Scanner scanner = new Scanner(System.in);
//        while (true) {
//
//            System.out.println("1. Wyświetl zawartosc");
//            System.out.println("2. save");
//            System.out.println("3. refresh");
//            System.out.println("4. wyswietl credentials.properties");
//
//            String input = scanner.nextLine();
//            switch (input) {
//                case "1" -> System.out.println(testConfig.getTest());
//                case "2" -> flameConfigService.update(TestConfig.class);
//                case "3" -> flameConfigService.refresh(TestConfig.class, false);
//                case "4" -> System.out.println(new PropertyLoader("credentials.properties").getProperty("mongo.uri"));
//                default -> System.out.println("Nieznana komenda");
//            }
//
//        }

  }

}
