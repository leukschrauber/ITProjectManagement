package at.uni.innsbruck.htibot.jpa.core.business.services;

import at.uni.innsbruck.htibot.core.business.services.KnowledgeService;
import at.uni.innsbruck.htibot.core.business.services.QPCLimitOffsetSort;
import at.uni.innsbruck.htibot.core.business.util.Logger;
import at.uni.innsbruck.htibot.core.exceptions.KnowledgeNotFoundException;
import at.uni.innsbruck.htibot.core.exceptions.PersistenceException;
import at.uni.innsbruck.htibot.core.model.enums.UserType;
import at.uni.innsbruck.htibot.core.model.knowledge.Knowledge;
import at.uni.innsbruck.htibot.core.model.knowledge.KnowledgeResource;
import at.uni.innsbruck.htibot.core.util.EmbeddingUtil;
import at.uni.innsbruck.htibot.core.util.properties.ConfigProperties;
import at.uni.innsbruck.htibot.jpa.common.services.JpaPersistenceService;
import at.uni.innsbruck.htibot.jpa.model.knowledge.JpaKnowledge;
import at.uni.innsbruck.htibot.jpa.model.knowledge.JpaKnowledge_;
import at.uni.innsbruck.htibot.security.ApiKeyRestricted;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;

@ApplicationScoped
@Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = Throwable.class)
public class JpaKnowledgeService extends
    JpaPersistenceService<Knowledge, JpaKnowledge, Long> implements KnowledgeService {

  @Serial
  private static final long serialVersionUID = -3507242399388356601L;

  @Inject
  private ConfigProperties configProperties;

  @Inject
  private Logger logger;


  @Override
  @NotNull
  @ApiKeyRestricted
  public <W extends Knowledge> W save(final @NotNull W entity) throws PersistenceException {
    return this._save(entity);
  }

  @Override
  @NotNull
  @ApiKeyRestricted
  public <W extends Knowledge> W update(final @NotNull W entity) throws PersistenceException {
    return this._update(entity);
  }

  @Override
  @NotNull
  @ApiKeyRestricted
  public <W extends Knowledge> W delete(final @NotNull W entity) throws PersistenceException {
    return this._delete(entity);
  }

  @Override
  @NotNull
  protected Class<JpaKnowledge> getPersistenceClass() {
    return JpaKnowledge.class;
  }

  @Override
  @NotNull
  protected Class<Knowledge> getInterfaceClass() {
    return Knowledge.class;
  }

  @Override
  @NotNull
  @ApiKeyRestricted
  public Optional<Knowledge> retrieveKnowledge(@NotNull final List<Double> questionVector) {
    final Pair<JpaKnowledge, Double> knowledgeAndSimilarity = this.executeResultListQuery(
            Tuple.class,
            JpaKnowledge.class,
            (query, cb, root) ->
                cb.isFalse(root.get(JpaKnowledge_.ARCHIVED)), null,
            (query, cb, root) -> query.multiselect(root, root.get(JpaKnowledge_.QUESTION_VECTOR)), true,
            QPCLimitOffsetSort.create()).stream()
        .map(tuple -> Pair.of(tuple.get(0, JpaKnowledge.class),
            EmbeddingUtil.computeCosineSimilarity(
                EmbeddingUtil.getAsEmbedding(tuple.get(1, String.class)), questionVector))).max(
            Comparator.comparing(Pair::getRight)).orElse(null);

    this.logger.info(
        String.format("Found knowledge with id %s as most similar. Cosine similarity is %s",
            Optional.ofNullable(knowledgeAndSimilarity)
                .flatMap(pair -> pair.getLeft().getFilename()).orElse(null),
            Optional.ofNullable(knowledgeAndSimilarity)
                .map(pair -> EmbeddingUtil.computeCosineSimilarity(questionVector,
                    pair.getLeft().getQuestionVector())).orElse(null)));

    if (knowledgeAndSimilarity != null && knowledgeAndSimilarity.getRight() != null
        && knowledgeAndSimilarity.getRight() > this.configProperties.getProperty(
        ConfigProperties.COSINE_SIMILARITY_TRESHOLD)) {
      return Optional.of(knowledgeAndSimilarity.getLeft());
    } else {
      return Optional.empty();
    }

  }

  @NotNull
  @Override
  public Knowledge createAndSave(@NotBlank final String questionVector,
      @NotBlank final String question,
      @NotBlank final String answer, @NotNull final UserType createdBy,
      @NotNull final Set<KnowledgeResource> knowledgeResourceSet, @NotNull final Boolean archived,
      final String filename)
      throws PersistenceException {
    final JpaKnowledge knowledge = new JpaKnowledge(questionVector, question, answer, createdBy,
        archived);
    knowledge.setKnowledgeResources(knowledgeResourceSet);
    knowledge.setFilename(filename);
    return this.save(knowledge);
  }

  @Override
  @NotNull
  public Knowledge archiveSystemKnowledge(@NotBlank final String filename)
      throws PersistenceException, KnowledgeNotFoundException {
    final Optional<Knowledge> knowledgeOptional = this.executeSingleResultQuery(
        (query, cb, root) -> cb.and(cb.equal(root.get(JpaKnowledge_.CREATED_BY), UserType.SYSTEM),
            cb.equal(root.get(JpaKnowledge_.ARCHIVED), Boolean.FALSE),
            cb.equal(root.get(JpaKnowledge_.FILENAME), filename)));

    if (knowledgeOptional.isEmpty()) {
      throw new KnowledgeNotFoundException("Trying to archive knowledge that is not existing.");
    }

    final Knowledge knowledge = knowledgeOptional.orElseThrow();
    knowledge.setArchived(Boolean.TRUE);

    return this._update(knowledge);
  }


  @Override
  public void archiveSystemKnowledge() throws PersistenceException {
    final List<Knowledge> knowledgeList = this.executeResultListQuery(
        (query, cb, root) -> cb.and(cb.equal(root.get(JpaKnowledge_.CREATED_BY), UserType.SYSTEM),
            cb.equal(root.get(JpaKnowledge_.ARCHIVED), Boolean.FALSE)), null,
        true, QPCLimitOffsetSort.create());

    knowledgeList.forEach(knowledge -> knowledge.setArchived(Boolean.TRUE));

    this._update(knowledgeList);
  }

  @NotNull
  @Override
  public List<String> getKnowledgeFileNames() {
    return this.executeResultListQuery(Tuple.class, JpaKnowledge.class,
        (query, cb, root) -> cb.and(cb.equal(root.get(JpaKnowledge_.CREATED_BY), UserType.SYSTEM),
            cb.isFalse(root.get(JpaKnowledge_.ARCHIVED))), null,
        (query, cb, root) -> query.multiselect(root.get(JpaKnowledge_.FILENAME)), true,
        QPCLimitOffsetSort.create()).stream().map(tuple -> tuple.get(0, String.class)).toList();
  }
}