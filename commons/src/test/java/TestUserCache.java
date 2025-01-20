import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.redis.RedisService;
import io.github.flamehub.commons.user.UserRepository;
import io.github.flamehub.commons.user.UserRedisCache;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class TestUserCache extends UserRedisCache<TestUser> {

  public TestUserCache(
      final RedisMessenger redisMessenger,
      final RedisService redisService,
      final UserRepository<TestUser> userRepository) {
    super(redisMessenger, redisService, TestUser.class, "test-users", Integer.MAX_VALUE,
        Duration.of(1, ChronoUnit.MINUTES), userRepository);
  }
}
