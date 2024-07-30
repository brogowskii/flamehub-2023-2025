import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigProperties;

@FlameConfigProperties(name = "test.json")
public final class TestConfig extends FlameConfig {

  private String test = "test";

  public String getTest() {
    return test;
  }

  public void setTest(String test) {
    this.test = test;
  }

}
