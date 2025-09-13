package io.github.flamehub.tiktok.video;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class TikTokVideoSorter {

  public static Set<TikTokVideo> sorted(
      final TikTokVideoSort sortType, final Collection<TikTokVideo> tikTokVideos) {
    final List<TikTokVideo> tikTokVideosSorted = new ArrayList<>(tikTokVideos);

    switch (sortType) {

      case LIKES -> tikTokVideosSorted.sort(
          (o1, o2) -> Integer.compare(o2.getDiggCount(), o1.getDiggCount())
      );

      case COMMENTS -> tikTokVideosSorted.sort(
          (o1, o2) -> Integer.compare(o2.getCommentCount(), o1.getCommentCount())
      );

      case VIEWS -> tikTokVideosSorted.sort(
          (o1, o2) -> Integer.compare(o2.getPlayCount(), o1.getPlayCount())
      );

      case NEWEST -> tikTokVideosSorted.sort((o1, o2) ->
          Long.compare(
              o2.getCreateTime(),
              o1.getCreateTime())
      );

      case OLDEST -> tikTokVideosSorted.sort(Comparator.comparingLong(TikTokVideo::getCreateTime));

      case NONE -> {
      }

    }

    return new HashSet<>(tikTokVideosSorted);

  }

}
