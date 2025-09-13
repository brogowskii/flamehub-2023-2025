package io.github.flamehub.commons.util;

public class Tuple<L, R> {

  private final L left;
  private final R right;

  private Tuple(final L left, final R right) {
    this.left = left;
    this.right = right;
  }

  public static <L, R> Tuple<L, R> of(final L left, final R right) {
    return new Tuple<>(left, right);
  }

  public L getLeft() {
    return left;
  }

  public R getRight() {
    return right;
  }


}
