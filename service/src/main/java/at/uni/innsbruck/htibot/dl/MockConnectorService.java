package at.uni.innsbruck.htibot.dl;

import at.uni.innsbruck.htibot.core.business.services.ConnectorService;
import at.uni.innsbruck.htibot.core.model.conversation.Conversation;
import at.uni.innsbruck.htibot.core.model.conversation.Message;
import at.uni.innsbruck.htibot.core.model.enums.ConversationLanguage;
import at.uni.innsbruck.htibot.core.model.knowledge.Knowledge;
import at.uni.innsbruck.htibot.security.ApiKeyRestricted;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;

public class MockConnectorService implements ConnectorService {

  @Override
  @NotBlank
  @ApiKeyRestricted
  public String getAnswer(@NotBlank final String prompt,
      final @NotNull Optional<Knowledge> knowledge,
      @NotNull final Optional<Conversation> conversation,
      @NotNull final ConversationLanguage language, final boolean close) {

    final StringBuilder sb = new StringBuilder();
    sb.append("This is an answer from a mocked OpenAI-Service.").append("\n\n");

    sb.append("You have asked me: ").append(prompt).append(".\n\n");

    if (knowledge.isPresent()) {
      sb.append("I will use this answer to reply: ").append(knowledge.orElseThrow().getAnswer())
          .append("\n\n");
    } else {
      sb.append("I have not found any FAQ I could use to answer. ").append("\n\n");
    }

    if (conversation.isPresent()) {
      sb.append(
              String.format("This is part of a conversation in which there were %s messages.",
                  conversation.orElseThrow().getMessages().size()))
          .append("\n");
    } else {
      sb.append("This is the beginning of a conversation.").append("\n\n");
    }

    sb.append(String.format(
            "Your language is %s. However, this is mock mode and everything is in English.",
            language.name()))
        .append("\n\n");

    if (close) {
      sb.append("I have been asked to close this conversation").append("\n\n");
    }

    return sb.toString();
  }

  @Override
  @NotBlank
  @ApiKeyRestricted
  public String translate(@NotBlank final String prompt, @NotNull final ConversationLanguage from,
      @NotNull final ConversationLanguage to) {
    return prompt;
  }

  @Override
  @NotBlank
  public String translateToEnglish(@NotBlank final String prompt) {
    return prompt;
  }

  @Override
  @NotBlank
  @ApiKeyRestricted
  public String generateIncidentReport(final @NotNull Conversation conversation) {
    final StringBuilder sb = new StringBuilder();
    sb.append(String.format("Mocked Incident Report for Conversation with id %s%n%n",
        conversation.getId()));

    for (final Message message : conversation.getMessages()) {
      sb.append(String.format("Messagy by %s ", message.getCreatedBy().name()))
          .append(message.getMessage()).append("\n\n");
    }
    return sb.toString();
  }

}
