package io.github.flamehub.commons.bukkit.censure;

import io.github.flamehub.commons.config.EnableRemote;
import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigProperties;
import java.util.List;

@FlameConfigProperties(name = "censure.json")
@EnableRemote(collection = "configs")
public final class CensureConfig extends FlameConfig {

  private List<String> censureReplacementList = List.of(
      "kurwa", "kvrwa", "kurewko", "kurwisko", "kurwi", "kurwo", "kurwie", "kurwię",
      "jebana", "jebany", "jebal", "jebał", "jebie", "jebac", "jebać",
      "pierdolic", "pierdolić", "pierdolenie", "pierdol",
      "cwel", "cwelu", "cweluszku", "cweluchu", "cweluch", "cveluch", "cvel", "cvelu", "cweloza",
      "chuj", "huj", "chujowe", "hujowe", "chujowy", "hujowy", "chujnia", "hujnia", "chvj", "chuja",
      "huja", "chujek", "chuju", "chujem",
      "chujowa", "hujowa", "cipa", "cipe", "cipą", "cipie", "dojebać", "dojebac", "dojebie",
      "dojebal", "dojebał", "dojebalem", "dojebie", "dojebałem",
      "dopierdalał", "dopierdalal", "dopierdalala", "dopierdalała", "dopierdolil", "dopierdolił",
      "odpierdala", "odpierdalal", "odpierdalał", "odpierdalala", "odpierdalała", "odpierdolic",
      "odpierdolić",
      "odkurwić", "odkurwic"
  );

  public List<String> getCensureReplacementList() {
    return censureReplacementList;
  }

}
