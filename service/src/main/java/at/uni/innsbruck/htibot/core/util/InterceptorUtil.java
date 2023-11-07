package at.uni.innsbruck.htibot.core.util;

import jakarta.interceptor.InvocationContext;
import java.util.Optional;

public final class InterceptorUtil {

  private InterceptorUtil() {
    // static
  }

  public static <V> Optional<V> getFirstParameterMatching(final Class<V> clazz, final InvocationContext ctx) {

    final Object[] parameters = ctx.getParameters();

    for (final Object object : parameters) {

      if (clazz.isInstance(object)) {

        @SuppressWarnings("unchecked") final V value = (V) object;

        return Optional.ofNullable(value);

      }

    }

    return Optional.empty();
  }

}
