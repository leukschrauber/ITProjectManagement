package at.uni.innsbruck.htibot.core.util;

import at.uni.innsbruck.htibot.core.model.IdHolder;
import jakarta.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public final class ObjectUtil {

  private ObjectUtil() {

  }

  @SafeVarargs
  public static <T, W> int hashCode(final T object, final Function<T, W>... getters) {

    final HashCodeBuilder builder = new HashCodeBuilder();

    for (final Function<T, W> getter : getters) {
      builder.append(getter.apply(object));
    }

    return builder.toHashCode();

  }

  public static <F, S, R> R nullChecker(final F first, final S second, final Function<S, R> firstNull, final Function<F, R> secondNull,
                                        final Supplier<R> bothNull,
                                        final BiFunction<F, S, R> nonNull) {
    if (first == null) {

      return second == null ? bothNull.get() : firstNull.apply(second);

    } else if (second == null) {

      return secondNull.apply(first);

    } else {

      return nonNull.apply(first, second);

    }
  }

  public static <T, F extends Comparable<F>> int compareWithNullCheck(final T first, final T second,
                                                                      final Function<T, F> compareFunction) {

    return ObjectUtil.compareWithNullCheck((f, s) -> {

      final F valueFirst = compareFunction.apply(f);
      final F valueSecond = compareFunction.apply(s);

      return ObjectUtil.compareWithNullCheck(Comparable::compareTo, valueFirst, valueSecond);

    }, first, second);

  }

  public static <T, F extends Comparable<F>, E extends Comparable<E>> int compareWithNullCheck(final T first, final T second,
                                                                                               final Function<T, F> firstCompareFunction,
                                                                                               final Function<T, E> secondCompareFunction) {

    return ObjectUtil.compareWithNullCheck((f, s) -> {

      int ret = ObjectUtil.compareWithNullCheck(Comparable::compareTo, firstCompareFunction.apply(f), firstCompareFunction
          .apply(s));

      if (ret == 0) {

        ret = ObjectUtil.compareWithNullCheck(Comparable::compareTo, secondCompareFunction.apply(f), secondCompareFunction
            .apply(s));

      }

      return ret;

    }, first, second);

  }

  public static <T, F extends Comparable<F>, E extends Comparable<E>, G extends Comparable<G>> int compareWithNullCheck(final T first,
                                                                                                                        final T second,
                                                                                                                        final Function<T, F> firstCompareFunction,
                                                                                                                        final Function<T, E> secondCompareFunction,
                                                                                                                        final Function<T, G> thirdCompareFunction) {

    final int ret = ObjectUtil.compareWithNullCheck(first, second, firstCompareFunction, secondCompareFunction);

    return ret == 0
           ? ObjectUtil.compareWithNullCheck(Comparable::compareTo, thirdCompareFunction.apply(first),
                                             thirdCompareFunction.apply(second))
           : ret;

  }

  public static <T> int compareWithNullCheck(final BiFunction<T, T, Integer> compareFunction, final T first, final T second) {

    return ObjectUtil.nullChecker(first, second, t -> -1, t -> 1, () -> 0, compareFunction);

  }

  public static <F, S> Boolean equalsWithNullCheck(final BiFunction<F, S, Boolean> equalsFunction, final F first, final S second) {

    final Function<S, Boolean> firstNull = t -> false;
    final Function<F, Boolean> secondNull = t -> false;
    final Supplier<Boolean> bothNull = () -> true;

    return ObjectUtil.nullChecker(first, second, firstNull, secondNull, bothNull, equalsFunction);

  }

  @SafeVarargs
  private static <T, W> boolean _checkGetters(final T first, final T second, final Function<T, W>... getters) {

    for (final Function<T, W> getter : getters) {

      if (!ObjectUtil.equalsWithNullCheck(Object::equals, getter.apply(first), getter.apply(second))) {
        return false;
      }

    }

    return true;

  }

  public static <T, W> boolean ifSameClass(final T first, final W second, final BiFunction<T, T, Boolean> furtherCompareFunction) {

    try {

      @SuppressWarnings({"unchecked"}) final T secondCasted = (T) second;

      return furtherCompareFunction != null ? furtherCompareFunction.apply(first, secondCasted) : false;

    } catch (final ClassCastException e) {
      return false;
    }

  }

  public static <T extends IdHolder<?>, W> boolean isSameClassWithSameId(final T first, final W second) {

    return ObjectUtil.ifSameClass(first, second, (f, s) -> {

                                    final Object firstId = f.getId();

                                    final Object secondId = s.getId();

                                    if (firstId == null) {

                                      return secondId == null;

                                    } else if (secondId == null) {

                                      return false;

                                    } else {

                                      return firstId.equals(secondId);

                                    }
                                  }

    );

  }

  public static <T> boolean equalsWithNullCheck(final T first, final T second) {

    return (first == second) || (ObjectUtil.nullChecker(first, second, f -> false, s -> false, () -> true,
                                                        (f, s) -> ObjectUtil.ifSameClass(first, second, Object::equals)));

  }

  @SafeVarargs
  public static <T, W> boolean equalsWithNullCheck(final T first, final W second, final Function<T, W> mandatoryGetter,
                                                   final Function<T, W>... comparingGetters) {

    return (first == second)
        || ObjectUtil.ifSameClass(first, second, (f, s) -> ObjectUtil._checkGetters(f, s,
                                                                                    ArrayUtils.insert(0,
                                                                                                      comparingGetters,
                                                                                                      mandatoryGetter)));

  }

  @SuppressWarnings("unchecked")
  public static <T, W> boolean equalsWithNullCheck(final BiFunction<T, W, Boolean> equalsFunction, final T first, final W second,
                                                   final BiFunction<T, T, Boolean> extension) {

    return ObjectUtil.equalsWithNullCheck(equalsFunction, first, second) && extension.apply(first, (T) second);

  }

  @SafeVarargs
  public static <T, W> boolean equalsWithNullCheck(final BiFunction<T, W, Boolean> equalsFunction, final T first, final W second,
                                                   final Function<T, W>... getters) {

    return ObjectUtil.equalsWithNullCheck(equalsFunction, first, second, (f, s) -> ObjectUtil._checkGetters(f, s, getters));

  }

  @SafeVarargs
  public static <T extends IdHolder<?>, W> boolean equalsWithNullCheck(final T first, final W second,
                                                                       final Function<T, W> mandatoryGetter,
                                                                       final Function<T, W>... getters) {

    return ObjectUtil.equalsWithNullCheck(ObjectUtil::isSameClassWithSameId, first, second,
                                          ArrayUtils.insert(0, getters, mandatoryGetter));

  }

  @SuppressWarnings("unchecked")
  public static <T extends IdHolder<?>, W> boolean equalsWithNullCheck(final T first, final W second,
                                                                       final BiFunction<T, T, Boolean> extension) {

    return ObjectUtil.equalsWithNullCheck(ObjectUtil::isSameClassWithSameId, first, second)
        && extension.apply(first, (T) second);

  }

  public static <O extends Comparable<? super O>> O minMaxOrElse(@NotNull final O min, @NotNull final O max, @NotNull final O fallback) {
    return ObjectUtils.min(ObjectUtils.max(min, fallback), max);
  }

  public static boolean isAnyNull(final Object... objects) {

    for (final Object o : objects) {
      if (Objects.isNull(o)) {
        return true;
      }
    }

    return false;

  }

  public static boolean isNoneNull(final Object... objects) {

    return !ObjectUtil.isAnyNull(objects);

  }

  public static boolean areAllNull(final Object... objects) {
    return Arrays.stream(objects)
                 .allMatch(Objects::isNull);
  }

  public static <T> T throwIfNull(final T object) {
    if (Objects.isNull(object)) {
      throw new IllegalArgumentException("null value not allowed");
    }
    return object;
  }
}
