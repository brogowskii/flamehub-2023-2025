package io.github.flamehub.code;

import io.github.flamehub.commons.user.UserFactory;

final class CodeUserFactory extends UserFactory<CodeUser> {

  CodeUserFactory() {
    super(CodeUser::new);
  }
}
