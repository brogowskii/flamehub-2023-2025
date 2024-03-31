package io.github.flamehub.timeplayed.user;

import io.github.flamehub.commons.user.UserFactory;

public final class TimePlayedUserFactory extends UserFactory<TimePlayedUser> {
    public TimePlayedUserFactory() {
        super(TimePlayedUser::new);
    }
}
