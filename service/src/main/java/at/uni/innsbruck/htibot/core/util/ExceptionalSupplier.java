package at.uni.innsbruck.htibot.core.util;

public interface ExceptionalSupplier<T> {

  /**
   * Gets a result.
   *
   * @return a result
   */
  T get() throws Exception;
}
