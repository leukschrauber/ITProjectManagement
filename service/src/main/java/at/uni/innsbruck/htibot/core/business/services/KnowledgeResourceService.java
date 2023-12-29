package at.uni.innsbruck.htibot.core.business.services;

import at.uni.innsbruck.htibot.core.exceptions.PersistenceException;
import at.uni.innsbruck.htibot.core.model.enums.UserType;
import at.uni.innsbruck.htibot.core.model.knowledge.Knowledge;
import at.uni.innsbruck.htibot.core.model.knowledge.KnowledgeResource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface KnowledgeResourceService extends PersistenceService<KnowledgeResource, Long> {

  @NotNull
  KnowledgeResource createAndSave(@NotBlank String resourcePath, @NotNull UserType userType,
      @NotNull
      Knowledge knowledge)
      throws PersistenceException;

}
