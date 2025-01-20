package io.github.flamehub.proxy.core.auth.user;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;
import dev.morphia.annotations.Transient;
import io.github.flamehub.commons.user.User;
import io.github.flamehub.commons.util.RandomStringGenerator;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Entity("auth_users")
public final class AuthUser extends User {

  private String password;
  @Indexed
  private String firstIP;

  @Indexed
  private String lastIP;

  private Map<String, Date> ipHistory;

  private boolean vpnAllowed;

  @Transient
  private transient String captcha;

  private boolean autoLogin = true;
  private boolean premium;

  @Transient
  private transient boolean logged;

  private Date lastLoginDate;
  private Date firstLoginDate;
  private Instant connectionDelay;

  public AuthUser() {
    this.captcha = RandomStringGenerator.generateStringWFromRandomCharacters(
        ThreadLocalRandom.current().nextInt(4, 7)
    );
    this.connectionDelay = Instant.now();
  }

  public AuthUser(final UUID uniqueId, final String name) {
    super(uniqueId, name);
    this.captcha = RandomStringGenerator.generateStringWFromRandomCharacters(
        ThreadLocalRandom.current().nextInt(4, 7)
    );
    this.connectionDelay = Instant.now();
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
    this.logged  = logged;
  }

  public String getLastIP() {
    return lastIP;
  }

  public void setLastIP(String lastIP) {
    this.lastIP = lastIP;
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

  public void setFirstLoginDate(Date firstLoginDate) {
    this.firstLoginDate = firstLoginDate;
  }

  public Instant getConnectionDelay() {
    return connectionDelay;
  }

  public void setConnectionDelay(Instant connectionDelay) {
    this.connectionDelay = connectionDelay;
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
