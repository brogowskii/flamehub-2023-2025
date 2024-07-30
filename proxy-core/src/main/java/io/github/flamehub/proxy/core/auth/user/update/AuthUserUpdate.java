package io.github.flamehub.proxy.core.auth.user.update;

import io.github.flamehub.commons.updater.ObjectUpdate;
import java.util.Map;

public final class AuthUserUpdate extends ObjectUpdate {

  public AuthUserUpdate(Object id, Map<String, Object> fieldValueMap) {
    super(id, fieldValueMap);
  }
}
