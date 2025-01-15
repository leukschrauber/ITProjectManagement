package at.uni.innsbruck.htibot.core.business.services;

import at.uni.innsbruck.htibot.core.model.conversation.Conversation;
import at.uni.innsbruck.htibot.core.model.knowledge.Knowledge;
import at.uni.innsbruck.htibot.security.ApiKeyRestricted;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

public interface ConnectorService {

  @NotBlank
  @ApiKeyRestricted
  String getAnswer(final @NotBlank String prompt, final @NotNull Optional<Knowledge> knowledge,
      @NotNull final Optional<Conversation> conversation,
      boolean close);

  @NotNull
  List<Float> getEmbedding(final @NotBlank String prompt);
}
