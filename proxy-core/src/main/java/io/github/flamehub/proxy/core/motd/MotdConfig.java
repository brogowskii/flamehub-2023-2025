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

  private List<String> sample = List.of(
      "example"
  );

  @JsonIgnore
  public String getFormattedMotd() {
    StringBuilder formattedMotd = new StringBuilder();

    if (first != null && !first.isEmpty()) {
      formattedMotd.append(first).append('\n');
    }

    if (second != null && !second.isEmpty()) {
      formattedMotd.append(second);
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