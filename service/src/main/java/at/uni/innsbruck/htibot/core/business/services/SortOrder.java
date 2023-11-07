package at.uni.innsbruck.htibot.core.business.services;

import java.util.Objects;

public enum SortOrder {

  ASCENDING, DESCENDING;

  public static SortOrder orDefault(final SortOrder sortOrder, final SortOrder defaultValue) {

    return Objects.isNull(sortOrder)
           ? defaultValue
           : sortOrder;


  }

  public static SortOrder orAsc(final SortOrder sortOrder) {
    return SortOrder.orDefault(sortOrder, SortOrder.ASCENDING);
  }

  public static SortOrder orDesc(final SortOrder sortOrder) {
    return SortOrder.orDefault(sortOrder, SortOrder.DESCENDING);
  }


  public static boolean isAscWithNullCheck(final SortOrder sortOrder) {
    return Objects.nonNull(sortOrder) && sortOrder == SortOrder.ASCENDING;
  }

  public static boolean isDescWithNullCheck(final SortOrder sortOrder) {
    return Objects.nonNull(sortOrder) && sortOrder == SortOrder.DESCENDING;
  }

  public static <T> T ifDescending(final SortOrder order, final T ifDescending, final T otherWise) {
    return SortOrder.isDescWithNullCheck(order) ? ifDescending : otherWise;
  }

  public static <T> T ifAscending(final SortOrder order, final T ifAscending, final T otherWise) {
    return SortOrder.isAscWithNullCheck(order) ? ifAscending : otherWise;
  }
}
