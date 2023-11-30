package at.uni.innsbruck.htibot.dl;

import at.uni.innsbruck.htibot.core.business.services.ConnectorService;
import at.uni.innsbruck.htibot.core.model.conversation.Conversation;
import at.uni.innsbruck.htibot.core.model.enums.UserType;
import at.uni.innsbruck.htibot.core.model.knowledge.Knowledge;
import at.uni.innsbruck.htibot.core.util.properties.ConfigPropertyBuilder;
import at.uni.innsbruck.htibot.rest.generated.model.LanguageEnum;
import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatMessage;
import com.azure.ai.openai.models.ChatRole;
import com.azure.core.credential.AzureKeyCredential;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class OpenAIConnectorService implements ConnectorService {

  private static final int MAX_TOKENS = 400;
  private static final double TEMPERATURE = 0.2;
  private static final double TOP_P = 0.95;
  private static final double FREQUENCY_PENALTY = 0;
  private static final double PRESENCE_PENALTY = 0;

  private static final String KNOWLEDGEABLE_BOT_MESSAGE =
      "I am HT-Bot, a customer service helpdesk employee. It is my primary goal to provide excellent support and assistance to users. I am following these guidelines:\n"
          + "Problem Solving: I am using my knowledge base to offer solutions or troubleshooting steps for common issues. If the problem is complex, I guide the user on the next steps or escalate the issue to a higher level of support if necessary.\n"
          + "Politeness: I maintain a polite and professional tone throughout the conversation. If the user is frustrated or upset, I express empathy and assure them that I am committed to resolving their concerns.\n"
          + "Knowledge Transfer: If the issue requires specialized knowledge or escalation, I provide relevant information to ensure a smooth handoff to other support channels.\n"
          + "I always remember that my goal is to create a positive and helpful customer experience. If I encounter a situation beyond my capabilities, I escalate it appropriately."
          + "This is the knowledge I will be using to resolve this issue: %s. I will not be disclosing to the user that I am using any provided knowledge to provide a seamless experience.";
  private static final ChatMessage CLOSING_BOT_MESSAGE = new ChatMessage(ChatRole.SYSTEM,
                                                                         "I am HT-Bot, a customer service helpdesk employee. It is my primary goal to provide excellent support and assistance to users. I am following these guidelines:\n"
                                                                             + "Problem Solving: I am using my knowledge base to offer solutions or troubleshooting steps for common issues. If the problem is complex, I guide the user on the next steps or escalate the issue to a higher level of support if necessary.\n"
                                                                             + "Politeness: I maintain a polite and professional tone throughout the conversation. If the user is frustrated or upset, I express empathy and assure them that I am committed to resolving their concerns.\n"
                                                                             + "Knowledge Transfer: If the issue requires specialized knowledge or escalation, I provide relevant information to ensure a smooth handoff to other support channels.\n"
                                                                             + "I do not know the answer to this problem and thus will create a report on the incident and ask the user to contact HTI HelpDesk.");
  private static final ChatMessage NO_CLUE_BOT_MESSAGE = new ChatMessage(ChatRole.SYSTEM,
                                                                         "I am HT-Bot, a customer service helpdesk employee. It is my primary goal to provide excellent support and assistance to users. I am following these guidelines:\n"
                                                                             + "Politeness: I maintain a polite and professional tone throughout the conversation. If the user is frustrated or upset, I express empathy and assure them that I am committed to resolving their concerns.\n"
                                                                             + "Inquiry: Prompt the user to provide details about their issue or question. Encourage them to be specific to ensure you can understand and address their needs accurately.\n"
                                                                             + "I always remember that my goal is to create a positive and helpful customer experience. If I encounter a situation beyond my capabilities, I escalate it appropriately."
                                                                             + "However, I do not know the solution to this problem yet and ask to user to provide more details about the problem at hand.");

  private OpenAIClient openAIClient;

  private String deploymentId;

  @PostConstruct
  public void init() {
    final String azureOpenaiKey = ConfigPropertyBuilder.property("at.uni.innsbruck.htibot.openai.connector.token");
    final String endpoint = ConfigPropertyBuilder.property("at.uni.innsbruck.htibot.openai.connector.host");
    this.deploymentId = ConfigPropertyBuilder.property("at.uni.innsbruck.htibot.openai.connector.deploymentid");

    this.openAIClient = new OpenAIClientBuilder()
        .endpoint(endpoint)
        .credential(new AzureKeyCredential(azureOpenaiKey))
        .buildClient();
  }

  @Override
  @NotBlank
  public String getAnswer(@NotBlank final String prompt, final @NotNull Optional<Knowledge> knowledge,
                          @NotNull final Optional<Conversation> conversation,
                          @NotNull final LanguageEnum language, final boolean close) {

    final List<ChatMessage> messageList = new ArrayList<>();
    if (close) {
      messageList.add(CLOSING_BOT_MESSAGE);
    } else if (knowledge.isEmpty()) {
      messageList.add(NO_CLUE_BOT_MESSAGE);
    } else {
      messageList.add(new ChatMessage(ChatRole.SYSTEM, String.format(KNOWLEDGEABLE_BOT_MESSAGE, knowledge.orElseThrow().getAnswer())));
    }

    if (conversation.isPresent()) {
      messageList.addAll(conversation.orElseThrow().getMessages().stream().map(
                                         message -> new ChatMessage(UserType.USER.equals(message.getCreatedBy()) ? ChatRole.USER : ChatRole.SYSTEM, message.getMessage()))
                                     .toList());
    }

    return this.openAIClient.getChatCompletions(this.deploymentId,
                                                new ChatCompletionsOptions(messageList).setMaxTokens(MAX_TOKENS).setTemperature(TEMPERATURE)
                                                                                       .setTopP(TOP_P)
                                                                                       .setFrequencyPenalty(
                                                                                           FREQUENCY_PENALTY).setPresencePenalty(
                                                                                           PRESENCE_PENALTY).setStop(Collections.emptyList())).getChoices().stream().findFirst()
                            .orElseThrow().getMessage().getContent();
  }

  @Override
  @NotBlank
  public String translate(@NotBlank final String prompt, @NotNull final LanguageEnum from, @NotNull final LanguageEnum to) {
    return null;
  }
}
