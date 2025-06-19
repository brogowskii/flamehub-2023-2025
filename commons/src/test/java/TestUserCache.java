import io.github.flamehub.commons.messenger.RedisMessenger;
import io.github.flamehub.commons.redis.RedisService;
import io.github.flamehub.commons.user.UserRepository;
import io.github.flamehub.commons.user.UserRedisCache;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class TestUserCache extends UserRedisCache<TestUser> {

  public TestUserCache(final RedisMessenger redisMessenger, final RedisService redisService,
      final Class<TestUser> type, final String namespace,
      final UserRepository<TestUser> userRepository) {
    super(redisMessenger, redisService, type, namespace, userRepository);
  }
}
