package at.uni.innsbruck.htibot.jpa.core.business.services;

import at.uni.innsbruck.htibot.core.business.services.KnowledgeService;
import at.uni.innsbruck.htibot.core.exceptions.PersistenceException;
import at.uni.innsbruck.htibot.core.model.enums.UserType;
import at.uni.innsbruck.htibot.core.model.knowledge.Knowledge;
import at.uni.innsbruck.htibot.jpa.common.services.JpaPersistenceService;
import at.uni.innsbruck.htibot.jpa.model.knowledge.JpaKnowledge;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.util.Optional;

@ApplicationScoped
@Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = Throwable.class)
public class JpaKnowledgeService extends
    JpaPersistenceService<Knowledge, JpaKnowledge, Long> implements KnowledgeService {

  @Serial
  private static final long serialVersionUID = -3507242399388356601L;


  @Override
  @NotNull
  public <W extends Knowledge> W save(final @NotNull W entity) throws PersistenceException {
    return this._save(entity);
  }

  @Override
  @NotNull
  public <W extends Knowledge> W update(final @NotNull W entity) throws PersistenceException {
    return this._update(entity);
  }

  @Override
  @NotNull
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
  public Optional<Knowledge> retrieveKnowledge(@NotBlank final String questionVector) {
    return Optional.ofNullable(
        new JpaKnowledge(questionVector, "SAP - No typing memory", "Close SAP\n"
            + "Delete the file SAPHistoryUSERLOGON.db in the folder : C:\\Users\\USERLOGON\\AppData\\Roaming\\SAP\\SAP GUI\\History\n"
            + "Open SAP again and do a first search in a field to create a history and then see if it keeps the input in memory.\n",
            UserType.SYSTEM));
  }
}