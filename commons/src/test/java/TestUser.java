import dev.morphia.annotations.Entity;
import io.github.flamehub.commons.user.User;
import java.util.UUID;

@Entity("test_users")
public class TestUser extends User {

  private int points = 1000;

  public TestUser() {
  }

  public TestUser(final UUID uniqueId, final String name) {
    super(uniqueId, name);
  }

  public int getPoints() {
    return points;
  }

  public void setPoints(final int points) {
    this.points = points;
  }
}
