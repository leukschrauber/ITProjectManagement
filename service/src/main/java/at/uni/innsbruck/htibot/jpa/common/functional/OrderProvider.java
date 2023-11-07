package at.uni.innsbruck.htibot.jpa.common.functional;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;
import java.util.List;

public interface OrderProvider<W> {

  List<Order> getOrder(CriteriaBuilder cb, Root<W> root);

}
