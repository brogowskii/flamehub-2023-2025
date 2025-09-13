package io.github.flamehub.commons.util;

public class Either<L, R> {

  private final L left;
  private final R right;

  private Either(final L left, final R right) {
    this.left = left;
    this.right = right;
  }

  public static <L, R> Either<L, R> left(final L left) {
    return new Either<>(left, null);
  }

  public static <L, R> Either<L, R> right(final R right) {
    return new Either<>(null, right);
  }

  public boolean isLeft() {
    return left != null;
  }

  public boolean isRight() {
    return right != null;
  }

  public L getLeft() {
    return left;
  }

  public R getRight() {
    return right;
  }

}
