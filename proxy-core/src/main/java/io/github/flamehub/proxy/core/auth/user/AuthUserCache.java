package io.github.flamehub.proxy.core.auth.user;

import com.google.common.base.Strings;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class AuthUserCache {

    private final Map<String, AuthUser> authUserMap = new ConcurrentHashMap<>();
    private final AuthUserRepository authUserRepository;

    public AuthUserCache(AuthUserRepository authUserRepository) {
        this.authUserRepository = authUserRepository;
    }

    public void add(AuthUser authUser) {
        this.authUserMap.put(authUser.getName().toLowerCase(), authUser);
    }

    public void remove(AuthUser authUser) {
        this.authUserMap.remove(authUser.getName().toLowerCase());
    }

    public AuthUser findByName(String name) {
        AuthUser authUser = this.authUserMap.get(name.toLowerCase());
        if (authUser == null) {
            authUser = this.authUserRepository.load(name);
        }

        return authUser;
    }

    @NotNull
    public List<AuthUser> findAccountsByIP(String ip) {
        if (Strings.isNullOrEmpty(ip)) {
            return List.of();
        }

        return this.authUserRepository.loadAll("ipAddress", ip)
                .stream()
                .filter(AuthUser::isRegistered)
                .collect(Collectors.toList());
    }

}
