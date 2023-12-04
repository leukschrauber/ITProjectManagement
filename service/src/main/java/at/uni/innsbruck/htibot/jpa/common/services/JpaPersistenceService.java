package at.uni.innsbruck.htibot.jpa.common.services;

import at.uni.innsbruck.htibot.core.business.services.PersistenceService;
import at.uni.innsbruck.htibot.core.business.services.QPCLimitOffsetSort;
import at.uni.innsbruck.htibot.core.business.util.Logger;
import at.uni.innsbruck.htibot.core.exceptions.PersistenceException;
import at.uni.innsbruck.htibot.core.model.IdHolder;
import at.uni.innsbruck.htibot.core.util.Converter;
import at.uni.innsbruck.htibot.core.util.TriFunction;
import at.uni.innsbruck.htibot.jpa.common.functional.ExtendedPredicateProvider;
import at.uni.innsbruck.htibot.jpa.common.functional.OrderProvider;
import at.uni.innsbruck.htibot.jpa.common.functional.Selector;
import at.uni.innsbruck.htibot.jpa.common.functional.SubquerySelector;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceContextType;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;

@Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = Throwable.class)
public abstract class JpaPersistenceService<T extends IdHolder<V>, U extends T, V extends Comparable<V>> implements
    PersistenceService<T, V> {

  private static final long serialVersionUID = 6605871483494404140L;

  @PersistenceContext(unitName = "htiBotDs", type = PersistenceContextType.TRANSACTION)
  private EntityManager entityManager;

  @Inject
  private Logger logger;

  private EntityManager getEntityManager() {
    return this.entityManager;
  }

  protected Logger getLogger() {
    return this.logger;
  }

  protected <X> X _save(final X entity) throws PersistenceException {
    try {
      this.getEntityManager().persist(entity);
      return entity;
    } catch (final Exception e) {
      throw new PersistenceException(e);
    }
  }

  protected <X> X _delete(final X entity) throws PersistenceException {
    try {
      this.getEntityManager()
          .remove(this.getEntityManager().contains(entity) ? entity
              : this.getEntityManager().merge(entity));

      return entity;
    } catch (final Exception e) {
      throw new PersistenceException(e);
    }
  }

  protected <X> X _update(final X entity) throws PersistenceException {
    try {
      return this.getEntityManager().merge(entity);
    } catch (final Exception e) {
      throw new PersistenceException(e);
    }
  }

  protected <X> List<X> _save(@NotNull final Collection<X> entities) throws PersistenceException {
    return this.performBatchOperation(entities, this::_save);
  }

  protected <X> Set<X> _save(@NotNull final Set<X> entities) throws PersistenceException {
    return this.performBatchOperation(entities, this::_save);
  }

  protected <X> List<X> _update(@NotNull final Collection<X> entities) throws PersistenceException {
    return this.performBatchOperation(entities, this::_update);
  }

  protected <X> Set<X> _update(@NotNull final Set<X> entities) throws PersistenceException {
    return this.performBatchOperation(entities, this::_update);
  }

  protected <X> List<X> _delete(@NotNull final Collection<X> entities) throws PersistenceException {
    return this.performBatchOperation(entities, this::_delete);
  }

  protected <X> Set<X> _delete(@NotNull final Set<X> entities) throws PersistenceException {
    return this.performBatchOperation(entities, this::_delete);
  }

  protected <X> List<X> performBatchOperation(@NotNull final Collection<X> entities,
      @NotNull final BatchOperation<X> operation)
      throws PersistenceException {
    return this.performBatchOperation(entities, operation, new ArrayList<>(entities.size()));
  }

  protected <X> Set<X> performBatchOperation(@NotNull final Set<X> entities,
      @NotNull final BatchOperation<X> operation)
      throws PersistenceException {
    return this.performBatchOperation(entities, operation, new HashSet<>(entities.size()));
  }

  protected <X, Y extends Collection<X>> Y performBatchOperation(
      @NotNull final Collection<X> entities,
      @NotNull final BatchOperation<X> operation,
      @NotNull final Y resultCollection)
      throws PersistenceException {

    if (CollectionUtils.isNotEmpty(entities)) {

      int i = 0;
      for (final X entity : entities) {

        final X temp = operation.apply(entity);
        resultCollection.add(temp);

        if ((i > 0) && ((i % 1000) == 0)) {
          this.getEntityManager().flush();
          this.getEntityManager().clear();
        }

        i++;
      }

    }

    return resultCollection;
  }

  @SuppressWarnings("unchecked")
  protected <X> X unproxy(final X proxied) {
    if (proxied instanceof HibernateProxy) {
      Hibernate.initialize(proxied);
      return (X) ((HibernateProxy) proxied).getHibernateLazyInitializer().getImplementation();
    }
    return proxied;
  }

  protected Predicate createIgnoreCaseLikePredicateWithEmptyCheck(final CriteriaBuilder cb,
      final String like,
      final Expression<String> path) {
    return StringUtils.isEmpty(like) ? cb.conjunction()
        : this.createIgnoreCaseLikePredicate(cb, like, path);
  }

  protected Predicate createIgnoreCaseLikePredicate(final CriteriaBuilder cb, final String like,
      final Expression<String> path) {
    return cb.like(cb.lower(path), this.likify(like));
  }

  protected Predicate createIgnoreCaseLikePredicate(final CriteriaBuilder cb,
      final Optional<String> like, final Expression<String> path) {
    return like.map(n -> cb.like(cb.lower(path), this.likify(n))).orElse(null);
  }

  protected <X> Predicate createIgnoreCaseLikePredicate(final CriteriaBuilder cb,
      final Root<X> root,
      final String like, final SingularAttribute<? super X, String> attribute) {
    return this.createIgnoreCaseLikePredicate(cb, like, root.get(attribute));
  }

  protected Predicate combineWithNullCheck(final CriteriaBuilder cb,
      final Predicate fallback,
      final TriFunction<CriteriaBuilder, Predicate, Predicate, Predicate> combiner,
      final Predicate... predicates) {
    return Arrays.stream(predicates)
        .filter(Objects::nonNull)
        .reduce((f, s) -> combiner.apply(cb, f, s)).orElse(fallback);
  }

  protected Predicate combineWithNullCheck(final CriteriaBuilder cb,
      final Predicate fallback,
      final TriFunction<CriteriaBuilder, Predicate, Predicate, Predicate> combiner,
      final List<Predicate> predicates) {
    return predicates.stream()
        .filter(Objects::nonNull)
        .reduce((f, s) -> combiner.apply(cb, f, s)).orElse(fallback);
  }

  protected Predicate conjunctWithOptionals(@NotNull final CriteriaBuilder cb,
      final Predicate basePredicate,
      final Optional<Predicate>... optionalPredicates) {

    final Stream<Predicate> basePredicateStream = Stream.ofNullable(basePredicate);
    final Stream<Predicate> predicateStream = ArrayUtils.isEmpty(optionalPredicates)
        ? Stream.empty()
        : Stream.of(optionalPredicates).map(p -> p.orElse(null));

    return this.combineWithNullCheck(cb, cb.conjunction(), CriteriaBuilder::and,
        Stream.concat(basePredicateStream, predicateStream)
            .toArray(Predicate[]::new));
  }

  protected Predicate conjunctWithNullCheck(final CriteriaBuilder cb,
      final Predicate... predicates) {
    return this.combineWithNullCheck(cb, cb.conjunction(), CriteriaBuilder::and, predicates);
  }

  protected Predicate conjunctWithNullCheck(final CriteriaBuilder cb,
      final List<Predicate> predicates) {
    return this.combineWithNullCheck(cb, cb.conjunction(), CriteriaBuilder::and, predicates);
  }

  protected Predicate disjunctWithNullCheck(final CriteriaBuilder cb,
      final Predicate... predicates) {
    return this.combineWithNullCheck(cb, cb.disjunction(), CriteriaBuilder::or, predicates);
  }

  protected String likify(final Object prefix) {
    return this.likify(Converter.toString(prefix));
  }

  protected String likify(final String like) {
    if (like != null) {
      return '%' + like.toLowerCase() + '%';
    } else {
      return like;
    }
  }

  private <R> TypedQuery<R> addOffsetAndLimit(final QPCLimitOffsetSort qpcLimitOffsetSort,
      final TypedQuery<R> typedQuery) {

    if (Objects.isNull(qpcLimitOffsetSort)) {
      return typedQuery;
    }

    qpcLimitOffsetSort.getOffset().ifPresent(typedQuery::setFirstResult);

    qpcLimitOffsetSort.getLimit().ifPresent(typedQuery::setMaxResults);

    return typedQuery;
  }

  protected <P, R> Subquery<R> createSubQuery(final CriteriaQuery<?> query,
      final CriteriaBuilder cb,
      final Class<R> returnClass, final Class<P> persistenceClass,
      final ExtendedPredicateProvider<P> predicateProvider,
      final SubquerySelector<R, P> selector, final boolean distinct) {

    final Subquery<R> subquery = query.subquery(returnClass);
    final Root<P> subRoot = subquery.from(persistenceClass);

    if (Objects.nonNull(predicateProvider)) {
      subquery.where(predicateProvider.getPredicate(query, cb, subRoot));
    }

    if (Objects.nonNull(selector)) {
      selector.select(subquery, cb, subRoot);
    }

    subquery.distinct(distinct);

    return subquery;
  }

  protected <P, R> CriteriaQuery<R> createCriteriaQuery(final Class<R> returnClass,
      final Class<P> persistenceClass,
      final EntityManager em,
      final ExtendedPredicateProvider<P> predicateProvider,
      final OrderProvider<P> orderProvider,
      final Selector<R, P> selector, final boolean distinct) {

    final CriteriaBuilder cb = em.getCriteriaBuilder();

    final CriteriaQuery<R> query = cb.createQuery(returnClass);
    final Root<P> root = query.from(persistenceClass);

    if (orderProvider != null) {
      query.orderBy(orderProvider.getOrder(cb, root));
    }

    if (predicateProvider != null) {
      query.where(predicateProvider.getPredicate(query, cb, root));
    }

    if (selector != null) {
      selector.select(query, cb, root);
    }
    query.distinct(distinct);

    return query;
  }

  private <R, P> TypedQuery<R> createTypedQuery(final Class<R> returnClass,
      final Class<P> persistenceClass,
      final EntityManager em, final ExtendedPredicateProvider<P> predicateProvider,
      final OrderProvider<P> orderProvider,
      final Selector<R, P> selector, final boolean distinct) {
    return em.createQuery(
        this.createCriteriaQuery(returnClass, persistenceClass, em, predicateProvider,
            orderProvider, selector, distinct));
  }

  private <R, P> TypedQuery<R> createTypedQuery(final Class<R> returnClass,
      final Class<P> persistenceClass,
      final EntityManager em, final ExtendedPredicateProvider<P> predicateProvider,
      final OrderProvider<P> orderProvider,
      final Selector<R, P> selector, final boolean distinct,
      final QPCLimitOffsetSort qpcLimitOffsetSort) {
    return this.addOffsetAndLimit(qpcLimitOffsetSort, this.getEntityManager().createQuery(
        this.createCriteriaQuery(returnClass, persistenceClass, em, predicateProvider,
            orderProvider, selector, distinct)));
  }

  public interface BatchOperation<B> {

    B apply(B entity) throws PersistenceException;
  }

  protected abstract Class<U> getPersistenceClass();

  protected abstract Class<T> getInterfaceClass();

  protected <P> long executeCountQuery(final Class<P> persistenceClass,
      final ExtendedPredicateProvider<P> predicateProvider, final boolean distinct) {

    return this.createTypedQuery(Long.class, persistenceClass, this.getEntityManager(),
            predicateProvider, null,
            (q, cb, root) -> q.select(distinct ? cb.countDistinct(root) : cb.count(root)), false)
        .getSingleResult();

  }

  protected <P> long executeCountQuery(final Class<P> persistenceClass,
      final ExtendedPredicateProvider<P> predicateProvider,
      final Selector<Long, P> selector) {
    return this.createTypedQuery(Long.class, persistenceClass, this.getEntityManager(),
            predicateProvider, null,
            selector, false)
        .getSingleResult();
  }

  protected long executeCountQuery(final ExtendedPredicateProvider<U> predicateProvider,
      final boolean distinct) {

    return this.executeCountQuery(this.getPersistenceClass(), predicateProvider, distinct);

  }

  protected Optional<T> executeSingleResultQuery(
      final ExtendedPredicateProvider<U> predicateProvider) {

    return this.executeSingleResultQuery(this.getInterfaceClass(), this.getPersistenceClass(),
        predicateProvider,
        this.getDefaultSelector());

  }

  protected <R, P> Optional<R> executeSingleResultQuery(final Class<R> returnClass,
      final Class<P> persistenceClass,
      final ExtendedPredicateProvider<P> predicateProvider,
      final Selector<R, P> selector) {
    try {
      final R entity = this.createTypedQuery(returnClass, persistenceClass, this.getEntityManager(),
          predicateProvider,
          null, selector, false).getSingleResult();
      return Optional.ofNullable(entity);
    } catch (final NoResultException e) {
      return Optional.empty();
    }
  }

  protected List<T> executeResultListQuery(final ExtendedPredicateProvider<U> predicateProvider,
      final OrderProvider<U> orderProvider,
      final boolean distinct, final QPCLimitOffsetSort qpcLimitOffsetSort) {

    return this.executeResultListQuery(this.getInterfaceClass(), this.getPersistenceClass(),
        predicateProvider,
        orderProvider,
        this.getDefaultSelector(), distinct, qpcLimitOffsetSort);

  }

  protected <R, P> List<R> executeResultListQuery(final Class<R> returnClass,
      final Class<P> persistenceClass,
      final ExtendedPredicateProvider<P> predicateProvider,
      final OrderProvider<P> orderProvider, final Selector<R, P> selector,
      final boolean distinct, final QPCLimitOffsetSort qpcLimitOffsetSort) {

    return this.createTypedQuery(returnClass, persistenceClass, this.getEntityManager(),
        predicateProvider, orderProvider,
        selector, distinct, qpcLimitOffsetSort).getResultList();

  }

  protected Stream<T> executeResultStreamQuery(final ExtendedPredicateProvider<U> predicateProvider,
      final OrderProvider<U> orderProvider,
      final boolean distinct) {

    return this.executeResultStreamQuery(this.getInterfaceClass(), this.getPersistenceClass(),
        predicateProvider, orderProvider,
        this.getDefaultSelector(), distinct, null);
  }

  protected <R, P> Stream<R> executeResultStreamQuery(final Class<R> returnClass,
      final Class<P> persistenceClass,
      final ExtendedPredicateProvider<P> predicateProvider,
      final OrderProvider<P> orderProvider, final Selector<R, P> selector,
      final boolean distinct, final QPCLimitOffsetSort qpcLimitOffsetSort) {

    return this.createTypedQuery(returnClass, persistenceClass, this.getEntityManager(),
        predicateProvider, orderProvider,
        selector, distinct, qpcLimitOffsetSort).getResultStream();

  }

  @Override
  public Optional<T> getById(final V id) {
    return Optional.ofNullable(this.getEntityManager().find(this.getPersistenceClass(), id));
  }

  @Override
  @SuppressWarnings("unchecked")
  @NotNull
  public <W extends T> W reload(@NotNull final W entity) {
    return (W) (this.getById(entity.getId()).orElseThrow());
  }

  @Override
  @NotNull
  public <W extends T> W reloadDetached(@NotNull final W entity) {
    final W reloaded = this.reload(entity);

    this.getEntityManager().detach(reloaded);

    return reloaded;

  }

  @Override
  @NotNull
  public <W extends T, C extends Collection<W>> C reloadDetached(
      @NotNull final Collection<W> entities,
      @NotNull final C resultCollection) {
    return this.executeResultStreamQuery(this.getInterfaceClass(),
            this.getPersistenceClass(),
            (query, cb, root) -> CollectionUtils.isEmpty(entities) ? cb.disjunction()
                : root.in(entities),
            null,
            this.getDefaultSelector(),
            true, null)
        .map(e -> (W) e)
        .peek(this.getEntityManager()::detach)
        .collect(Collectors.toCollection(() -> resultCollection));
  }


  @Override
  @NotNull
  public <W extends T, C extends Collection<W>> C reload(@NotNull final Collection<W> entities,
      @NotNull final C resultCollection) {
    return this.executeResultStreamQuery(this.getInterfaceClass(),
            this.getPersistenceClass(),
            (query, cb, root) -> CollectionUtils.isEmpty(entities) ? cb.disjunction()
                : root.in(entities),
            null,
            this.getDefaultSelector(),
            true, null)
        .map(e -> (W) e)
        .collect(Collectors.toCollection(() -> resultCollection));
  }

  private Selector<T, U> getDefaultSelector() {
    return (q, cb, root) -> q.select(root);
  }

}
