package at.uni.innsbruck.htibot.jpa.core.business.services;

import at.uni.innsbruck.htibot.core.business.services.KnowledgeService;
import at.uni.innsbruck.htibot.core.business.services.QPCLimitOffsetSort;
import at.uni.innsbruck.htibot.core.exceptions.KnowledgeNotFoundException;
import at.uni.innsbruck.htibot.core.exceptions.PersistenceException;
import at.uni.innsbruck.htibot.core.model.enums.UserType;
import at.uni.innsbruck.htibot.core.model.knowledge.Knowledge;
import at.uni.innsbruck.htibot.core.model.knowledge.KnowledgeResource;
import at.uni.innsbruck.htibot.jpa.common.services.JpaPersistenceService;
import at.uni.innsbruck.htibot.jpa.model.knowledge.JpaKnowledge;
import at.uni.innsbruck.htibot.jpa.model.knowledge.JpaKnowledge_;
import at.uni.innsbruck.htibot.security.ApiKeyRestricted;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ApplicationScoped
@Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = Throwable.class)
public class JpaKnowledgeService extends
    JpaPersistenceService<Knowledge, JpaKnowledge, Long> implements KnowledgeService {

  @Serial
  private static final long serialVersionUID = -3507242399388356601L;


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
  public Optional<Knowledge> retrieveKnowledge(@NotBlank final String questionVector) {
    return Optional.ofNullable(
        new JpaKnowledge(questionVector, "SAP - No typing memory", "Close SAP\n"
            + "Delete the file SAPHistoryUSERLOGON.db in the folder : C:\\Users\\USERLOGON\\AppData\\Roaming\\SAP\\SAP GUI\\History\n"
            + "Open SAP again and do a first search in a field to create a history and then see if it keeps the input in memory.\n",
            UserType.SYSTEM, Boolean.FALSE));
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