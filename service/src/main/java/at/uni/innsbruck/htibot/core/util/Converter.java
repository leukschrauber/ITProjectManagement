package at.uni.innsbruck.htibot.core.util;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import org.apache.commons.collections.CollectionUtils;

public final class Converter {

  private Converter() {
    // static
  }

  public static <T extends Enum<T>> String toString(final T object) {
    return Converter.convert(object, Enum::name);
  }

  public static String toString(final Object object) {
    return Converter.convert(object, Object::toString);
  }

  public static <T, V> T convert(final V base, final Function<V, T> converter) {
    return Optional.ofNullable(base).flatMap(s -> Optional.ofNullable(converter.apply(s)))
        .orElse(null);
  }

  private static <T, V> T convert(final V base, final Function<V, T> converter,
      final T defaultValue) {
    return Optional.ofNullable(base).flatMap(s -> Optional.ofNullable(converter.apply(s)))
        .orElse(defaultValue);
  }

  public static Boolean toBoolean(final String base, final Boolean defaultValue) {
    return Converter.convert(base, Boolean::parseBoolean, defaultValue);
  }

  public static <T> String toString(final Collection<T> coll, final String delimiter) {

    if (CollectionUtils.isEmpty(coll)) {
      return null;

    } else {

      final StringBuilder sb = new StringBuilder();

      boolean first = true;

      for (final T t : coll) {

        if (first) {
          first = false;
        } else {
          sb.append(delimiter);
        }

        sb.append(t.toString());

      }
      return sb.toString();

    }

  }
}
