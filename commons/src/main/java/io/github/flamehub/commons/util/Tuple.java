package io.github.flamehub.commons.util;

public class Tuple<L, R> {
  private final L left;
  private final R right;

  private Tuple(L left, R right) {
    this.left = left;
    this.right = right;
  }

  public static <L, R> Tuple<L, R> of(L left, R right) {
    return new Tuple<>(left, right);
  }

  public L getLeft() {
    return left;
  }

  public R getRight() {
    return right;
  }


}
