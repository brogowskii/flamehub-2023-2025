package io.github.flamehub.voucher;

import io.github.flamehub.commons.config.EnableRemote;
import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigProperties;
import java.util.HashMap;
import java.util.Map;

@FlameConfigProperties(name = "voucher.json")
@EnableRemote(collection = "configs")
public final class VoucherConfig extends FlameConfig {

  private Map<String, Voucher> voucherMap = new HashMap<>();

  public VoucherConfig() {
  }

  public Map<String, Voucher> getVoucherMap() {
    return voucherMap;
  }
}
