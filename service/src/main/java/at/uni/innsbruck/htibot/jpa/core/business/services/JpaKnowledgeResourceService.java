package at.uni.innsbruck.htibot.jpa.core.business.services;

import at.uni.innsbruck.htibot.core.business.services.KnowledgeResourceService;
import at.uni.innsbruck.htibot.core.exceptions.PersistenceException;
import at.uni.innsbruck.htibot.core.model.enums.UserType;
import at.uni.innsbruck.htibot.core.model.knowledge.Knowledge;
import at.uni.innsbruck.htibot.core.model.knowledge.KnowledgeResource;
import at.uni.innsbruck.htibot.jpa.common.services.JpaPersistenceService;
import at.uni.innsbruck.htibot.jpa.model.knowledge.JpaKnowledgeResource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;

@ApplicationScoped
@Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = Throwable.class)
public class JpaKnowledgeResourceService extends
    JpaPersistenceService<KnowledgeResource, JpaKnowledgeResource, Long> implements
    KnowledgeResourceService {

  @Serial
  private static final long serialVersionUID = -3507242399388356601L;


  @Override
  @NotNull
  public <W extends KnowledgeResource> W save(final @NotNull W entity) throws PersistenceException {
    return this._save(entity);
  }

  @Override
  @NotNull
  public <W extends KnowledgeResource> W update(final @NotNull W entity)
      throws PersistenceException {
    return this._update(entity);
  }

  @Override
  @NotNull
  public <W extends KnowledgeResource> W delete(final @NotNull W entity)
      throws PersistenceException {
    return this._delete(entity);
  }

  @Override
  @NotNull
  protected Class<JpaKnowledgeResource> getPersistenceClass() {
    return JpaKnowledgeResource.class;
  }

  @Override
  @NotNull
  protected Class<KnowledgeResource> getInterfaceClass() {
    return KnowledgeResource.class;
  }

  @Override
  @NotNull
  public KnowledgeResource createAndSave(@NotBlank final String resourcePath,
      @NotNull final UserType userType, @NotNull final Knowledge knowledge)
      throws PersistenceException {
    return this.save(new JpaKnowledgeResource(resourcePath, userType, knowledge));
  }

}