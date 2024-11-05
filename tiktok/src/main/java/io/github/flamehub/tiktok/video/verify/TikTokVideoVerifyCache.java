package io.github.flamehub.tiktok.video.verify;

import io.github.flamehub.commons.cache.KeyValueCache;

public final class TikTokVideoVerifyCache extends KeyValueCache<String, TikTokVideoVerify> {

  public TikTokVideoVerifyCache() {
    super(true);
  }

}
