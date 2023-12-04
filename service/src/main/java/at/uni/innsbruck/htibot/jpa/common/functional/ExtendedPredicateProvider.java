package at.uni.innsbruck.htibot.jpa.common.functional;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import java.util.function.BinaryOperator;
import org.apache.commons.lang3.ObjectUtils;

public interface ExtendedPredicateProvider<W> {


  Predicate getPredicate(CriteriaQuery<?> query, CriteriaBuilder cb, Root<W> root);

  /**
   * Left precedence: e.g. x.or(y).and(z) = (x.or(y)).and(z)
   */
  default ExtendedPredicateProvider<W> and(@NotNull final ExtendedPredicateProvider<W> other) {
    return (query, cb, root) -> this.combine(cb::and, cb.conjunction(), other)
        .getPredicate(query, cb, root);
  }

  /**
   * Left precedence: e.g. x.or(y).and(z) = (x.or(y)).and(z)
   */
  default ExtendedPredicateProvider<W> or(@NotNull final ExtendedPredicateProvider<W> other) {
    return (query, cb, root) -> this.combine(cb::or, cb.disjunction(), other)
        .getPredicate(query, cb, root);
  }

  private ExtendedPredicateProvider<W> combine(@NotNull final BinaryOperator<Predicate> combiner,
      @NotNull final Predicate fallback,
      @NotNull final ExtendedPredicateProvider<W> other) {
    return (query, cb, root) -> {
      final Predicate thisPredicate = this.getPredicate(query, cb, root);

      final Predicate otherPredicate = other.getPredicate(query, cb, root);

      if (!ObjectUtils.anyNotNull(thisPredicate, otherPredicate)) {
        return fallback;
      }

      if (Objects.isNull(thisPredicate)) {
        return otherPredicate;
      }

      if (Objects.isNull(otherPredicate)) {
        return thisPredicate;
      }

      return combiner.apply(thisPredicate, otherPredicate);
    };
  }

}
