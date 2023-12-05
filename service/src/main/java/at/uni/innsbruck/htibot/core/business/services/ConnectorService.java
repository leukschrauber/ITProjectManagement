package at.uni.innsbruck.htibot.core.business.services;

import at.uni.innsbruck.htibot.core.exceptions.MaxMessagesExceededException;
import at.uni.innsbruck.htibot.core.model.conversation.Conversation;
import at.uni.innsbruck.htibot.core.model.knowledge.Knowledge;
import at.uni.innsbruck.htibot.rest.generated.model.LanguageEnum;
import at.uni.innsbruck.htibot.security.ApiKeyRestricted;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;

public interface ConnectorService {

  @NotBlank
  @ApiKeyRestricted
  String getAnswer(final @NotBlank String prompt, final @NotNull Optional<Knowledge> knowledge,
      @NotNull final Optional<Conversation> conversation, final @NotNull LanguageEnum language,
      boolean close)
      throws MaxMessagesExceededException;

  @NotBlank
  @ApiKeyRestricted
  String translate(final @NotBlank String prompt, final @NotNull LanguageEnum from,
      final @NotNull LanguageEnum to);
}
