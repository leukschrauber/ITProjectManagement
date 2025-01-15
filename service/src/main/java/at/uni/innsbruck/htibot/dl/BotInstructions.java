package at.uni.innsbruck.htibot.dl;

import com.azure.ai.openai.models.ChatRequestSystemMessage;
import com.azure.ai.openai.models.ChatRequestUserMessage;
import jakarta.validation.constraints.NotNull;

public class BotInstructions {

  private static final String KNOWLEDGEABLE_BOT_MESSAGE =
      "Act as an expert for laws regarding to leisure time outdoor activites in Tyrol, Austria. Use the following knowledge to answer the question: \n\n%s";
  
  //TODO: https://community.openai.com/t/system-message-how-to-force-chatgpt-api-to-follow-it/82775/7
  private static final ChatRequestUserMessage CLOSING_BOT_MESSAGE = new ChatRequestUserMessage(
                                                                                    "Act as an polite regulatory expert to leisure time outdoor activities in Tyrol, Austria. Do not ask me any questions. If my issues are not leisure time or outdoor related, your only job is to say that the question is not related. If the question is related, you categorize my question and provide possible laws to investigate in a fluent sentence. Never ask follow-up questions. Do not try to resolve my problem. Again: Do not ask any question.");


  @NotNull
  public ChatRequestSystemMessage getKnowledgeableBotMessage(final String knowledge) {
    return new ChatRequestSystemMessage(String.format(KNOWLEDGEABLE_BOT_MESSAGE, knowledge));
  }

  @NotNull
  public ChatRequestUserMessage getClosingBotMessage() {
    return CLOSING_BOT_MESSAGE;
  }

}
