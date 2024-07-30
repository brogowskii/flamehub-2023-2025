package io.github.flamehub.code;

import io.github.flamehub.commons.user.UserFactory;

public final class CodeUserFactory extends UserFactory<CodeUser> {

  public CodeUserFactory() {
    super(CodeUser::new);
  }
}
