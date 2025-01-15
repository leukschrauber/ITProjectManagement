package at.uni.innsbruck.htibot.dl;

import at.uni.innsbruck.htibot.core.business.services.ConnectorService;
import at.uni.innsbruck.htibot.core.model.conversation.Conversation;
import at.uni.innsbruck.htibot.core.model.enums.UserType;
import at.uni.innsbruck.htibot.core.model.knowledge.Knowledge;
import at.uni.innsbruck.htibot.core.util.properties.ConfigProperties;
import at.uni.innsbruck.htibot.security.ApiKeyRestricted;
import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatRequestAssistantMessage;
import com.azure.ai.openai.models.ChatRequestMessage;
import com.azure.ai.openai.models.ChatRequestUserMessage;
import com.azure.ai.openai.models.EmbeddingsOptions;
import com.azure.core.credential.AzureKeyCredential;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class OpenAIConnectorService implements ConnectorService {

  private static final int MAX_TOKENS = 3000;
  private static final double TEMPERATURE = 0.2;
  private static final double TOP_P = 0.95;
  private static final double FREQUENCY_PENALTY = 0;
  private static final double PRESENCE_PENALTY = 0;

  private final OpenAIClient openAIClient;
  private final String gptDeploymentId;
  private final String adaDeploymentId;

  private final ConfigProperties configProperties;

  private final BotInstructions englishBotInstructions;

  public OpenAIConnectorService(final ConfigProperties configProperties) {
    this.configProperties = configProperties;
    this.englishBotInstructions = new BotInstructions();

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
      @NotNull final Optional<Conversation> conversation, final boolean close) {
      final List<ChatRequestMessage> messageList = new ArrayList<>();
    if (close || knowledge.isEmpty()) {
        messageList.add(this.englishBotInstructions.getClosingBotMessage());
      } else {
        messageList.add(
            this.englishBotInstructions.getKnowledgeableBotMessage(
                knowledge.orElseThrow().getAnswer()));
      }

      if (conversation.isPresent()) {
        messageList.addAll(conversation.orElseThrow().getMessages().stream().map(
                message -> UserType.USER.equals(message.getCreatedBy()) ? new ChatRequestUserMessage(message.getMessage()) : new ChatRequestAssistantMessage(message.getMessage()))
            .toList());
      }

      messageList.add(new ChatRequestUserMessage(prompt));

    return this.openAIClient.getChatCompletions(this.gptDeploymentId,
            new ChatCompletionsOptions(messageList).setMaxTokens(
                    MAX_TOKENS)
                  .setTemperature(TEMPERATURE)
                  .setTopP(TOP_P)
                  .setFrequencyPenalty(
                      FREQUENCY_PENALTY).setPresencePenalty(
                      PRESENCE_PENALTY).setStop(Collections.emptyList())).getChoices().stream().findFirst()
          .orElseThrow().getMessage().getContent();
  }



  @NotNull
  @Override
  public List<Float> getEmbedding(final @NotBlank String prompt) {
    return this.openAIClient.getEmbeddings(this.adaDeploymentId,
            new EmbeddingsOptions(Collections.singletonList(prompt)).setModel("text-embedding-ada-002"))
        .getData().stream().findFirst().orElseThrow().getEmbedding();
  }
}
