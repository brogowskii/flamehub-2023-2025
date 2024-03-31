package io.github.flamehub.lobby.daily;

import io.github.flamehub.commons.user.UserFactory;

public class DailyUserFactory extends UserFactory<DailyUser> {
    public DailyUserFactory() {
        super(DailyUser::new);
    }
}
