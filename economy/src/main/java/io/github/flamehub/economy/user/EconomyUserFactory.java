package io.github.flamehub.economy.user;

import io.github.flamehub.commons.user.UserFactory;

final class EconomyUserFactory extends UserFactory<EconomyUser> {
    EconomyUserFactory() {
        super(EconomyUser::new);
    }
}
