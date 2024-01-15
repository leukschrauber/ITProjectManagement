package at.uni.innsbruck.htibot.dl;

import at.uni.innsbruck.htibot.core.business.services.ConnectorService;
import at.uni.innsbruck.htibot.core.model.conversation.Conversation;
import at.uni.innsbruck.htibot.core.model.conversation.Message;
import at.uni.innsbruck.htibot.core.model.enums.ConversationLanguage;
import at.uni.innsbruck.htibot.core.model.enums.UserType;
import at.uni.innsbruck.htibot.core.model.knowledge.Knowledge;
import at.uni.innsbruck.htibot.core.util.properties.ConfigProperties;
import at.uni.innsbruck.htibot.dl.botinstructions.BotInstructionResolver;
import at.uni.innsbruck.htibot.security.ApiKeyRestricted;
import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatMessage;
import com.azure.ai.openai.models.ChatRole;
import com.azure.ai.openai.models.EmbeddingsOptions;
import com.azure.core.credential.AzureKeyCredential;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class OpenAIConnectorService implements ConnectorService {

  private static final int MAX_TOKENS = 400;
  private static final double TEMPERATURE = 0.2;
  private static final double TOP_P = 0.95;
  private static final double FREQUENCY_PENALTY = 0;
  private static final double PRESENCE_PENALTY = 0;

  private final OpenAIClient openAIClient;
  private final String gptDeploymentId;
  private final String adaDeploymentId;

  private final ConfigProperties configProperties;

  private final BotInstructionResolver botInstructionResolver;

  public OpenAIConnectorService(final ConfigProperties configProperties, final
  BotInstructionResolver botInstructionResolver) {
    this.configProperties = configProperties;
    this.botInstructionResolver = botInstructionResolver;

      final String azureOpenaiKey = this.configProperties.getProperty(
          ConfigProperties.OPENAI_TOKEN);
      final String endpoint = this.configProperties.getProperty(ConfigProperties.OPENAI_HOST);
    this.gptDeploymentId = this.configProperties.getProperty(
        ConfigProperties.OPENAI_GPT_DEPLOYMENT);
    this.adaDeploymentId = this.configProperties.getProperty(
        ConfigProperties.OPENAI_ADA_DEPLOYMENT);

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
      @NotNull final ConversationLanguage language, final boolean close) {
      final List<ChatMessage> messageList = new ArrayList<>();
    if (close || knowledge.isEmpty()) {
        messageList.add(this.botInstructionResolver.getClosingBotMessage(language));
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

      messageList.add(new ChatMessage(ChatRole.USER, prompt));

    return this.openAIClient.getChatCompletions(this.gptDeploymentId,
              new ChatCompletionsOptions(messageList).setMaxTokens(MAX_TOKENS)
                  .setTemperature(TEMPERATURE)
                  .setTopP(TOP_P)
                  .setFrequencyPenalty(
                      FREQUENCY_PENALTY).setPresencePenalty(
                      PRESENCE_PENALTY).setStop(Collections.emptyList())).getChoices().stream().findFirst()
          .orElseThrow().getMessage().getContent();
  }

  @Override
  @NotBlank
  @ApiKeyRestricted
  public String translate(@NotBlank final String prompt, @NotNull final ConversationLanguage from,
      @NotNull final ConversationLanguage to) {
      final List<ChatMessage> messageList = new ArrayList<>();
      messageList.add(
          this.botInstructionResolver.getTranslatingBotMessage(from, to));
      messageList.add(new ChatMessage(ChatRole.USER, prompt));

    return this.openAIClient.getChatCompletions(this.gptDeploymentId,
              new ChatCompletionsOptions(messageList).setMaxTokens(MAX_TOKENS)
                  .setTemperature(TEMPERATURE)
                  .setTopP(TOP_P)
                  .setFrequencyPenalty(
                      FREQUENCY_PENALTY).setPresencePenalty(
                      PRESENCE_PENALTY).setStop(Collections.emptyList())).getChoices().stream().findFirst()
          .orElseThrow().getMessage().getContent();
  }

  @Override
  @NotBlank
  public String translateToEnglish(@NotBlank final String prompt) {
    final List<ChatMessage> messageList = new ArrayList<>();
    messageList.add(
        this.botInstructionResolver.getLanguageTranslatingBotMessage(ConversationLanguage.ENGLISH));
    messageList.add(new ChatMessage(ChatRole.USER, prompt));

    return this.openAIClient.getChatCompletions(this.gptDeploymentId,
            new ChatCompletionsOptions(messageList).setMaxTokens(MAX_TOKENS)
                .setTemperature(TEMPERATURE)
                .setTopP(TOP_P)
                .setFrequencyPenalty(
                    FREQUENCY_PENALTY).setPresencePenalty(
                    PRESENCE_PENALTY).setStop(Collections.emptyList())).getChoices().stream().findFirst()
        .orElseThrow().getMessage().getContent();
  }

  @Override
  @NotBlank
  @ApiKeyRestricted
  public String generateIncidentReport(final @NotNull Conversation conversation) {
      final List<ChatMessage> messageList = new ArrayList<>();

    messageList.add(this.botInstructionResolver.getSummarizingBotMessage(conversation.getLanguage(),
        conversation.getMessages().stream().filter(msg -> UserType.USER.equals(msg.getCreatedBy()))
            .map(Message::getMessage).collect(Collectors.joining("\n")),
        conversation.getMessages().stream()
            .filter(msg -> UserType.SYSTEM.equals(msg.getCreatedBy()))
            .map(Message::getMessage).collect(Collectors.joining("\n"))));

    return this.openAIClient.getChatCompletions(this.gptDeploymentId,
              new ChatCompletionsOptions(messageList).setMaxTokens(MAX_TOKENS)
                  .setTemperature(TEMPERATURE)
                  .setTopP(TOP_P)
                  .setFrequencyPenalty(
                      FREQUENCY_PENALTY).setPresencePenalty(
                      PRESENCE_PENALTY).setStop(Collections.emptyList())).getChoices().stream().findFirst()
          .orElseThrow().getMessage().getContent();
  }

  @NotNull
  @Override
  public List<Double> getEmbedding(final @NotBlank String prompt) {
    return this.openAIClient.getEmbeddings(this.adaDeploymentId,
            new EmbeddingsOptions(Collections.singletonList(prompt)).setModel("text-embedding-ada-002"))
        .getData().stream().findFirst().orElseThrow().getEmbedding();
  }
}
