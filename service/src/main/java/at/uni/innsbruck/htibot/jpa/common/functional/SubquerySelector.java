package at.uni.innsbruck.htibot.jpa.common.functional;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

public interface SubquerySelector<R, P> {

  void select(Subquery<R> query, CriteriaBuilder cb, Root<P> root);
}
