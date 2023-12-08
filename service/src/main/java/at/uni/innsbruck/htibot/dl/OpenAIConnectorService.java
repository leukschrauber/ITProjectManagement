package at.uni.innsbruck.htibot.dl;

import at.uni.innsbruck.htibot.core.business.services.ConnectorService;
import at.uni.innsbruck.htibot.core.exceptions.MaxMessagesExceededException;
import at.uni.innsbruck.htibot.core.model.conversation.Conversation;
import at.uni.innsbruck.htibot.core.model.enums.ConversationLanguage;
import at.uni.innsbruck.htibot.core.model.enums.UserType;
import at.uni.innsbruck.htibot.core.model.knowledge.Knowledge;
import at.uni.innsbruck.htibot.core.util.ExceptionalSupplier;
import at.uni.innsbruck.htibot.core.util.properties.ConfigProperties;
import at.uni.innsbruck.htibot.dl.botinstructions.BotInstructionResolver;
import at.uni.innsbruck.htibot.security.ApiKeyRestricted;
import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatMessage;
import com.azure.ai.openai.models.ChatRole;
import com.azure.core.credential.AzureKeyCredential;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class OpenAIConnectorService implements ConnectorService {

  private static final int MAX_TOKENS = 400;
  private static final double TEMPERATURE = 0.2;
  private static final double TOP_P = 0.95;
  private static final double FREQUENCY_PENALTY = 0;
  private static final double PRESENCE_PENALTY = 0;

  private final OpenAIClient openAIClient;

  private final String deploymentId;

  private Pair<LocalDate, Integer> messageCounter;

  private final ConfigProperties configProperties;

  private final BotInstructionResolver botInstructionResolver;

  public OpenAIConnectorService(final ConfigProperties configProperties, final
  BotInstructionResolver botInstructionResolver) {
    this.configProperties = configProperties;
    this.botInstructionResolver = botInstructionResolver;

      final String azureOpenaiKey = this.configProperties.getProperty(
          ConfigProperties.OPENAI_TOKEN);
      final String endpoint = this.configProperties.getProperty(ConfigProperties.OPENAI_HOST);
    this.deploymentId = this.configProperties.getProperty(ConfigProperties.OPENAI_DEPLOYMENT);

      this.openAIClient = new OpenAIClientBuilder()
          .endpoint(endpoint)
          .credential(new AzureKeyCredential(azureOpenaiKey))
          .buildClient();
  }

  @Override
  @NotBlank
  @ApiKeyRestricted
  public String getAnswer(@NotBlank final String prompt,
      final @NotNull Optional<Knowledge> knowledge,
      @NotNull final Optional<Conversation> conversation,
      @NotNull final ConversationLanguage language, final boolean close)
      throws Exception {
    return this.handleMessageCount(() -> {
      final List<ChatMessage> messageList = new ArrayList<>();
      if (close) {
        messageList.add(this.botInstructionResolver.getClosingBotMessage(language));
      } else if (knowledge.isEmpty()) {
        messageList.add(this.botInstructionResolver.getNoClueBotMessage(language));
      } else {
        messageList.add(
            this.botInstructionResolver.getKnowledgeableBotMessage(
                knowledge.orElseThrow().getAnswer(),
                language));
      }

      if (conversation.isPresent()) {
        messageList.addAll(conversation.orElseThrow().getMessages().stream().map(
                message -> new ChatMessage(
                    UserType.USER.equals(message.getCreatedBy()) ? ChatRole.USER
                        : ChatRole.ASSISTANT,
                    message.getMessage()))
            .toList());
      }

      return this.openAIClient.getChatCompletions(this.deploymentId,
              new ChatCompletionsOptions(messageList).setMaxTokens(MAX_TOKENS)
                  .setTemperature(TEMPERATURE)
                  .setTopP(TOP_P)
                  .setFrequencyPenalty(
                      FREQUENCY_PENALTY).setPresencePenalty(
                      PRESENCE_PENALTY).setStop(Collections.emptyList())).getChoices().stream().findFirst()
          .orElseThrow().getMessage().getContent();
    });
  }

  @Override
  @NotBlank
  @ApiKeyRestricted
  public String translate(@NotBlank final String prompt, @NotNull final ConversationLanguage from,
      @NotNull final ConversationLanguage to) throws Exception {
    return this.handleMessageCount(() -> {
      final List<ChatMessage> messageList = new ArrayList<>();
      messageList.add(
          this.botInstructionResolver.getTranslatingBotMessage(from, to));
      messageList.add(new ChatMessage(ChatRole.USER, prompt));

      return this.openAIClient.getChatCompletions(this.deploymentId,
              new ChatCompletionsOptions(messageList).setMaxTokens(MAX_TOKENS)
                  .setTemperature(TEMPERATURE)
                  .setTopP(TOP_P)
                  .setFrequencyPenalty(
                      FREQUENCY_PENALTY).setPresencePenalty(
                      PRESENCE_PENALTY).setStop(Collections.emptyList())).getChoices().stream().findFirst()
          .orElseThrow().getMessage().getContent();
    });
  }

  @Override
  @NotBlank
  @ApiKeyRestricted
  public String generateIncidentReport(final @NotNull Conversation conversation) throws Exception {
    return this.handleMessageCount(() -> {
      final List<ChatMessage> messageList = new ArrayList<>();

      messageList.add(
          this.botInstructionResolver.getClosingBotMessage(
              conversation.getLanguage()));

      messageList.addAll(conversation.getMessages().stream().map(
              message -> new ChatMessage(
                  UserType.USER.equals(message.getCreatedBy()) ? ChatRole.USER : ChatRole.ASSISTANT,
                  message.getMessage()))
          .toList());

      return this.openAIClient.getChatCompletions(this.deploymentId,
              new ChatCompletionsOptions(messageList).setMaxTokens(MAX_TOKENS)
                  .setTemperature(TEMPERATURE)
                  .setTopP(TOP_P)
                  .setFrequencyPenalty(
                      FREQUENCY_PENALTY).setPresencePenalty(
                      PRESENCE_PENALTY).setStop(Collections.emptyList())).getChoices().stream().findFirst()
          .orElseThrow().getMessage().getContent();
    });
  }

  private String handleMessageCount(final ExceptionalSupplier<String> exceptionalRunnable)
      throws Exception {
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

    final String answer = exceptionalRunnable.get();

    if (this.configProperties.keyExists(
        ConfigProperties.OPENAI_MAX_MESSAGES.getKey())) {
      this.messageCounter.setValue(this.messageCounter.getValue() + 1);
    }

    return answer;
  }
}
