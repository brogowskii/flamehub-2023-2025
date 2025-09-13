package io.github.flamehub.commons.bukkit.tab;

import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@FlameConfigProperties(name = "tablist.json")
public final class TablistConfig extends FlameConfig {

  private List<String> header = new ArrayList<>();
  private List<String> footer = new ArrayList<>();

  private String headValue = "";
  private String headSignature = "";

  private boolean usePages = true;

  private Map<Integer, TablistPage> pages = Map.of(
      1, new TablistPage()
  );

  public List<String> getHeader() {
    return header;
  }

  public List<String> getFooter() {
    return footer;
  }

  public Map<Integer, TablistPage> getPages() {
    return pages;
  }

  public String getHeadValue() {
    return headValue;
  }

  public String getHeadSignature() {
    return headSignature;
  }

  public boolean isUsePages() {
    return usePages;
  }
}
