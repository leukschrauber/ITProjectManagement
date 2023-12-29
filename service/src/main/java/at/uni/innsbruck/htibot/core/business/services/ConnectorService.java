package at.uni.innsbruck.htibot.core.business.services;

import at.uni.innsbruck.htibot.core.model.conversation.Conversation;
import at.uni.innsbruck.htibot.core.model.enums.ConversationLanguage;
import at.uni.innsbruck.htibot.core.model.knowledge.Knowledge;
import at.uni.innsbruck.htibot.security.ApiKeyRestricted;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;

public interface ConnectorService {

  @NotBlank
  @ApiKeyRestricted
  String getAnswer(final @NotBlank String prompt, final @NotNull Optional<Knowledge> knowledge,
      @NotNull final Optional<Conversation> conversation,
      final @NotNull ConversationLanguage language,
      boolean close);

  @NotBlank
  @ApiKeyRestricted
  String translate(final @NotBlank String prompt, final @NotNull ConversationLanguage from,
      final @NotNull ConversationLanguage to);

  @NotBlank
  String translateToEnglish(final @NotBlank String prompt);

  @NotBlank
  @ApiKeyRestricted
  String generateIncidentReport(final @NotNull Conversation conversation);
}
