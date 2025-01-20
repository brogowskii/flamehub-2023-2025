import dev.morphia.Datastore;
import io.github.flamehub.commons.user.UserRepository;

public class TestUserRepository extends UserRepository<TestUser> {

  public TestUserRepository(final Datastore datastore) {
    super(datastore, TestUser.class);
  }
}
