package io.github.flamehub.proxy.core.auth.user;

import com.google.common.base.Strings;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class AuthUserCache {

    private final Map<UUID, AuthUser> authUsersByUniqueId = new ConcurrentHashMap<>();
    private final Map<String, AuthUser> authUsersByName = new ConcurrentHashMap<>();
    private final AuthUserRepository authUserRepository;

    public AuthUserCache(AuthUserRepository authUserRepository) {
        this.authUserRepository = authUserRepository;
    }

    public void add(AuthUser authUser) {
        this.authUsersByName.put(authUser.getName().toLowerCase(), authUser);
        this.authUsersByUniqueId.put(authUser.getUniqueId(), authUser);
    }

    public void remove(AuthUser authUser) {
        this.authUsersByName.remove(authUser.getName().toLowerCase());
        this.authUsersByUniqueId.remove(authUser.getUniqueId());
    }

    public void updateName(AuthUser user, String newName) {
        this.authUsersByName.remove(user.getName());
        this.authUsersByName.put(newName.toLowerCase(), user);

        user.setName(newName);
    }

    public AuthUser findByName(String name) {
        AuthUser authUser = this.authUsersByName.get(name.toLowerCase());
        if (authUser == null) {
            authUser = this.authUserRepository.loadIgnoreCase("name", name);
        }

        return authUser;
    }

    public AuthUser findByUniqueId(UUID uniqueId) {
        AuthUser authUser = this.authUsersByUniqueId.get(uniqueId);
        if (authUser == null) {
            authUser = this.authUserRepository.load(uniqueId);
        }

        return authUser;
    }

    @NotNull
    public List<AuthUser> findAccountsByIP(String ip) {
        if (Strings.isNullOrEmpty(ip)) {
            return List.of();
        }

        return this.authUserRepository.loadAll("lastIP", ip)
                .stream()
                .filter(authUser -> authUser.isRegistered() || authUser.isPremium())
                .collect(Collectors.toList());
    }

}
