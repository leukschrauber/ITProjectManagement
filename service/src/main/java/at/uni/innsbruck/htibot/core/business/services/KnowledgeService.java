package at.uni.innsbruck.htibot.core.business.services;

import at.uni.innsbruck.htibot.core.model.knowledge.Knowledge;
import jakarta.validation.constraints.NotBlank;
import java.util.Optional;

public interface KnowledgeService extends PersistenceService<Knowledge, Long> {

  Optional<Knowledge> retrieveKnowledge(@NotBlank String questionVector);

}
