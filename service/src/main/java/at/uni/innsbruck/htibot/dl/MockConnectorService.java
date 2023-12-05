package at.uni.innsbruck.htibot.dl;

import at.uni.innsbruck.htibot.core.business.services.ConnectorService;
import at.uni.innsbruck.htibot.core.exceptions.MaxMessagesExceededException;
import at.uni.innsbruck.htibot.core.model.conversation.Conversation;
import at.uni.innsbruck.htibot.core.model.knowledge.Knowledge;
import at.uni.innsbruck.htibot.core.util.properties.ConfigProperties;
import at.uni.innsbruck.htibot.rest.generated.model.LanguageEnum;
import at.uni.innsbruck.htibot.security.ApiKeyRestricted;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Optional;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class MockConnectorService implements ConnectorService {

  private Pair<LocalDate, Integer> messageCounter;

  private final ConfigProperties configProperties;

  public MockConnectorService(final ConfigProperties configProperties) {
    this.configProperties = configProperties;
  }

  @Override
  @NotBlank
  @ApiKeyRestricted
  public String getAnswer(@NotBlank final String prompt,
      final @NotNull Optional<Knowledge> knowledge,
      @NotNull final Optional<Conversation> conversation,
      @NotNull final LanguageEnum language, final boolean close)
      throws MaxMessagesExceededException {

    if ((this.messageCounter == null || !this.messageCounter.getLeft().equals(LocalDate.now()))
        && this.configProperties.keyExists(
        ConfigProperties.OPENAI_MAX_MESSAGES.getKey())) {
      this.messageCounter = new MutablePair<>(LocalDate.now(), 0);
    }

    if (this.configProperties.keyExists(
        ConfigProperties.OPENAI_MAX_MESSAGES.getKey())
        && this.messageCounter.getRight() > this.configProperties.getProperty(
        ConfigProperties.OPENAI_MAX_MESSAGES)) {
      throw new MaxMessagesExceededException(
          String.format("Maximum messages exceeded for today. Try again tomorrow. Max Messages: %s",
              this.configProperties.getProperty(
                  ConfigProperties.OPENAI_MAX_MESSAGES)));
    }

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

    if (this.configProperties.keyExists(
        ConfigProperties.OPENAI_MAX_MESSAGES.getKey())) {
      this.messageCounter.setValue(this.messageCounter.getValue() + 1);
    }

    return sb.toString();
  }

  @Override
  @NotBlank
  @ApiKeyRestricted
  public String translate(@NotBlank final String prompt, @NotNull final LanguageEnum from,
      @NotNull final LanguageEnum to) {
    return null;
  }

}
