package io.github.flamehub.proxy.core.vpn;

import eu.okaeri.sdk.noproxy.NoProxyClient;
import eu.okaeri.sdk.noproxy.model.NoProxyAddressInfo;

public final class VPNDetector {

  private final static String TOKEN = "6d01768b-2e1f-4b68-893f-0f7f420f0f41";

  private final static NoProxyClient client;

  static {
    client = new NoProxyClient(TOKEN);
  }

  public static NoProxyAddressInfo getInfo(String ip) {
    return client.getInfo(ip);

  }

}
