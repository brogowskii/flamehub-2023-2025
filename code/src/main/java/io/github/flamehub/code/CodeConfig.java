package io.github.flamehub.code;

import io.github.flamehub.commons.config.EnableRemote;
import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigProperties;
import java.util.ArrayList;
import java.util.List;

@FlameConfigProperties(name = "code.json")
@EnableRemote(collection = "configs")
public final class CodeConfig extends FlameConfig {

  private List<Code> codes = List.of(
      new Code("vip", List.of("lp user {PLAYER} parent addtemp vip 2d"), new ArrayList<>(), "30m"));

  public CodeConfig() {
  }

  public Code findByName(String name) {

    for (Code code : this.codes) {
      if (code.getName().equalsIgnoreCase(name)) {
        return code;
      }
    }
    return null;

  }

  public List<Code> getCodes() {
    return codes;
  }

}
