package at.uni.innsbruck.htibot.core.business.services;

import java.util.Optional;

public class QPCLimitOffsetSort extends QPCAbstractBase {


  private static final long serialVersionUID = -7523783021606621616L;

  private Integer offset;

  private Integer limit;

  private SortOrder sortOrder;

  private String sortField;

  private QPCLimitOffsetSort() {
    // noop
  }

  public static QPCLimitOffsetSort create() {
    return new QPCLimitOffsetSort();
  }

  public static QPCLimitOffsetSort create(final boolean dirty) {

    final QPCLimitOffsetSort qpc = create();

    if (dirty) {
      qpc.markDirty();
    }

    return qpc;

  }

  public Optional<String> getSortField() {
    return Optional.ofNullable(this.sortField);
  }

  public QPCLimitOffsetSort setSortField(final String sortField) {
    this.sortField = this.checkIfShouldMarkDirty(this.sortField, sortField);
    return this;
  }

  public String getSortFieldOrNull() {
    return this.sortField;
  }

  public Optional<SortOrder> getSortOrder() {
    return Optional.ofNullable(this.sortOrder);
  }

  public QPCLimitOffsetSort setSortOrder(final SortOrder sortorder) {
    this.sortOrder = this.checkIfShouldMarkDirty(this.sortOrder, sortorder);
    return this;
  }

  public SortOrder getSortOrderOrNull() {
    return this.sortOrder;
  }

  public Optional<Integer> getOffset() {
    return Optional.ofNullable(this.offset);
  }

  public QPCLimitOffsetSort setOffset(final Integer offset) {
    this.offset = this.checkIfShouldMarkDirty(this.offset, offset);
    return this;
  }

  public Integer getOffsetOrNull() {
    return this.offset;
  }

  public Optional<Integer> getLimit() {
    return Optional.ofNullable(this.limit);
  }

  public QPCLimitOffsetSort setLimit(final Integer limit) {
    this.limit = this.checkIfShouldMarkDirty(this.limit, limit);
    return this;
  }

  public Integer getLimitOrNull() {
    return this.limit;
  }
}
