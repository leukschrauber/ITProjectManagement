package at.uni.innsbruck.htibot.core.business.services;

import at.uni.innsbruck.htibot.core.exceptions.PersistenceException;
import at.uni.innsbruck.htibot.core.model.enums.UserType;
import at.uni.innsbruck.htibot.core.model.knowledge.Knowledge;
import at.uni.innsbruck.htibot.core.model.knowledge.KnowledgeResource;
import at.uni.innsbruck.htibot.security.ApiKeyRestricted;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import java.util.Set;

public interface KnowledgeService extends PersistenceService<Knowledge, Long> {

  @ApiKeyRestricted
  Optional<Knowledge> retrieveKnowledge(@NotBlank String questionVector);

  @NotNull
  Knowledge createAndSave(@NotBlank String questionVector, @NotBlank String question,
      @NotBlank String answer, @NotNull UserType createdBy,
      @NotNull Set<KnowledgeResource> knowledgeResourceSet, @NotNull Boolean archived,
      String filename)
      throws PersistenceException;

  void archiveSystemKnowledge() throws PersistenceException;

}
