package io.github.flamehub.proxy.core.auth.user.update;

import io.github.flamehub.commons.messenger.packet.PacketHandler;
import io.github.flamehub.proxy.core.auth.user.AuthUser;
import io.github.flamehub.proxy.core.auth.user.AuthUserCache;
import io.github.flamehub.proxy.core.auth.user.AuthUserRepository;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class AuthUserUpdateHandler {

  private final Lock lock = new ReentrantLock();

  private final AuthUserCache authUserCache;
  private final AuthUserRepository authUserRepository;

  public AuthUserUpdateHandler(AuthUserCache authUserCache, AuthUserRepository authUserRepository) {
    this.authUserCache = authUserCache;
    this.authUserRepository = authUserRepository;
  }

  @PacketHandler
  public void handle(AuthUserUpdate update) {

    this.lock.lock();
    try {

      AuthUser authUser = this.authUserCache.findByName((String) update.getId());
      for (Map.Entry<String, Object> entry : update.getFieldValueMap().entrySet()) {
        Field field = authUser.getClass().getField(entry.getKey());
        field.setAccessible(true);
        field.set(authUser, entry.getValue());
      }

      this.authUserRepository.save(authUser);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      e.printStackTrace();
    } finally {
      this.lock.unlock();
    }
  }


}
