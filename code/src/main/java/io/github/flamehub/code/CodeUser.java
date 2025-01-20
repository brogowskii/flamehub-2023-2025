package io.github.flamehub.code;

import dev.morphia.annotations.Entity;
import io.github.flamehub.commons.user.User;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity("code_users")
final class CodeUser extends User {

  private final Set<String> receivedCodes = new HashSet<>();

  public CodeUser() {
  }

  public CodeUser(final UUID uniqueId, final String name) {
    super(uniqueId, name);
  }

  public Set<String> getReceivedCodes() {
    return receivedCodes;
  }
}
