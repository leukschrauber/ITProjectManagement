package at.uni.innsbruck.htibot.jpa.common.functional;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public interface Selector<R, P> {

  void select(CriteriaQuery<R> query, CriteriaBuilder cb, Root<P> root);
}
