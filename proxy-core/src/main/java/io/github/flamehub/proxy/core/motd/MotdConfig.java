package io.github.flamehub.proxy.core.motd;


import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.flamehub.commons.config.EnableRemote;
import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigProperties;
import java.util.Arrays;
import java.util.List;

@FlameConfigProperties(name = "motd.json")
@EnableRemote(collection = "configs")
public final class MotdConfig extends FlameConfig {

  private String first = "line1";
  private String second = "line2";

  private List<String> sample = Arrays.asList(
      "example"
  );

  @JsonIgnore
  public String getFormattedMotd() {
    StringBuilder formattedMotd = new StringBuilder();

    if (this.first != null && !this.first.isEmpty()) {
      formattedMotd.append(this.first).append('\n');
    }

    if (this.second != null && !this.second.isEmpty()) {
      formattedMotd.append(this.second);
    }

    return formattedMotd.toString();
  }

  public String getFirst() {
    return first;
  }

  public String getSecond() {
    return second;
  }

  public List<String> getSample() {
    return sample;
  }
}