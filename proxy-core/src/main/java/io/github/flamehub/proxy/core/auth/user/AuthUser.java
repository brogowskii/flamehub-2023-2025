package io.github.flamehub.proxy.core.auth.user;

import dev.morphia.annotations.*;
import io.github.flamehub.commons.util.RandomStringGenerator;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Entity("auth_users")
public final class AuthUser {

    @Id
    private UUID uniqueId;

    @Indexed
    private String name;
    private String password;

    @Indexed
    private String firstIP;

    @Indexed
    private String lastIP;

    private Map<String, Date> ipHistory;

    private boolean vpnAllowed;

    @Transient
    private String captcha;

    private boolean autoLogin = true;
    private boolean premium;

    @Transient
    private boolean logged;


    private Date lastLoginDate;
    private Date firstLoginDate;
    private Instant connectionDelay;

    public AuthUser() {
        this.captcha = RandomStringGenerator.generateStringWFromRandomCharacters(
                ThreadLocalRandom.current().nextInt(4, 7)
        );
        this.connectionDelay = Instant.now();
    }

    public AuthUser(UUID uniqueId, String name) {
        this();
        this.uniqueId = uniqueId;
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

    public String getFirstIP() {
        return firstIP;
    }

    public void setFirstIP(String firstIP) {
        this.firstIP = firstIP;
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
        return password != null && !password.isEmpty();
    }

    public boolean isLogged() {
        return logged;
    }

    public void setLogged(boolean logged) {
        this.logged = logged;
    }

    public String getLastIP() {
        return lastIP;
    }

    public Map<String, Date> getIpHistory() {
        if (ipHistory == null) {
            ipHistory = new HashMap<>();
        }
        return ipHistory;
    }

    public Date getFirstLoginDate() {
        return firstLoginDate;
    }

    public Instant getConnectionDelay() {
        return connectionDelay;
    }

    public void setConnectionDelay(Instant connectionDelay) {
        this.connectionDelay = connectionDelay;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public void setFirstLoginDate(Date firstLoginDate) {
        this.firstLoginDate = firstLoginDate;
    }


    public void setLastIP(String lastIP) {
        this.lastIP = lastIP;
    }

    public boolean isVpnAllowed() {
        return vpnAllowed;
    }

    public void setVpnAllowed(boolean vpnAllowed) {
        this.vpnAllowed = vpnAllowed;
    }

    public boolean isAutoLogin() {
        return autoLogin;
    }

    public void setAutoLogin(boolean autoLogin) {
        this.autoLogin = autoLogin;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }
}
