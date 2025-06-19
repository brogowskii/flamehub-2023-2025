package io.github.flamehub.commons.user;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;
import java.util.UUID;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.PROPERTY,
    property = "@class"
)
public class User {

  @Id
  private UUID uniqueId;

  @Indexed
  private String name;

  public User() {
  }

  public User(final UUID uniqueId, final String name) {
    this.uniqueId = uniqueId;
    this.name = name;
  }

  public UUID getUniqueId() {
    return uniqueId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
