package io.github.flamehub.proxy.core.auth.user;

import dev.morphia.annotations.*;
import io.github.flamehub.commons.util.RandomStringGenerator;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

@Entity("auth_users")
public final class AuthUser {

    @Id
    private String name;
    private String password;

    @Indexed
    private String ipAddress;

    @Transient
    private String captcha;

    private boolean premium;
    private boolean registered;

    @Transient
    private boolean logged;

    private Instant firstJoinTime;
    private Instant connectionDelay;

    public AuthUser() {
        this.captcha = RandomStringGenerator.generateStringWFromRandomCharacters(
                ThreadLocalRandom.current().nextInt(4, 7)
        );
        this.connectionDelay = Instant.now();
    }

    public AuthUser(String name) {
        this();
        this.name = name;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    public boolean isLogged() {
        return logged;
    }

    public void setLogged(boolean logged) {
        this.logged = logged;
    }

    public Instant getFirstJoinTime() {
        return firstJoinTime;
    }

    public void setFirstJoinTime(Instant firstJoinTime) {
        this.firstJoinTime = firstJoinTime;
    }

    public Instant getConnectionDelay() {
        return connectionDelay;
    }

    public void setConnectionDelay(Instant connectionDelay) {
        this.connectionDelay = connectionDelay;
    }
}
