package io.github.flamehub.commons.server;

import java.util.function.UnaryOperator;

public abstract class NetworkServerUpdater implements Runnable {

  protected final NetworkServerFacade networkServerFacade;

  protected NetworkServerUpdater(final NetworkServerFacade networkServerFacade) {
    this.networkServerFacade = networkServerFacade;
  }

  public boolean update(final UnaryOperator<NetworkServer> mutator) {
    final NetworkServer current = networkServerFacade.getCurrent();
    if (current == null) {
      return false;
    }

    try {
      final NetworkServer modified = mutator.apply(current);
      final NetworkServer toStore = (modified != null ? modified : current);
      networkServerFacade.put(toStore.getName(), toStore);
      return true;
    } catch (final Exception e) {
      e.printStackTrace();
      return false;
    }
  }

}
