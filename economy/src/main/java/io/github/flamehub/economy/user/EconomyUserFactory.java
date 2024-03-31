package io.github.flamehub.economy.user;

import io.github.flamehub.commons.user.UserFactory;

public class EconomyUserFactory extends UserFactory<EconomyUser> {
    public EconomyUserFactory() {
        super(EconomyUser::new);
    }
}
