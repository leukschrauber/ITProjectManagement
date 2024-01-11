package at.uni.innsbruck.htibot.dl.botinstructions;

import at.uni.innsbruck.htibot.core.model.enums.ConversationLanguage;
import com.azure.ai.openai.models.ChatMessage;
import com.azure.ai.openai.models.ChatRole;
import jakarta.validation.constraints.NotNull;

public class EnglishBotInstructions implements BotInstructions {

  private static final String KNOWLEDGEABLE_BOT_MESSAGE =
      "Act as a customer service helpdesk employee. It is your primary goal to provide excellent support and assistance to users. You are following these guidelines:\n"
          + "Problem Solving: You are using my knowledge base to offer solutions or troubleshooting steps for common issues. If the problem is complex, you guide the user on the next steps.\n"
          + "Politeness: You maintain a polite and professional tone throughout the conversation. If the user is frustrated or upset, you express empathy and assure them that you are committed to resolving their concerns.\n"
          + "You always remember that your goal is to create a positive and helpful customer experience."
          + "This is the knowledge you will be using to resolve this issue. You will not use any other knowledge to help the user and you will not be disclosing to the user that you are using any provided knowledge to provide a seamless experience.\n\n%s";
  
  //TODO: https://community.openai.com/t/system-message-how-to-force-chatgpt-api-to-follow-it/82775/7
  private static final ChatMessage CLOSING_BOT_MESSAGE = new ChatMessage(ChatRole.USER,
      "Act as an polite assistant to a helpdesk employee. Do not ask me any questions. If my issues are not IT-related, your only job is to say that the question is not IT-related. If the question is IT-related, you categorize my incident for your colleagues and provide a shortlist of possible root causes. Never ask follow-up questions. Do not try to resolve my problem. Again: Do not ask any question.");
  private static final String TRANSLATING_BOT_MESSAGE = "You are a language translator specializing in %s to %s translations. Provide accurate and natural translations for the given input.";

  private static final ChatMessage LANGUAGE_TRANSLATING_BOT_MESSAGE = new ChatMessage(
      ChatRole.SYSTEM,
      "You are a language translator specializing in translations to English. Provide accurate and natural english translations for the given input.");


  @Override
  public ConversationLanguage getLanguage() {
    return ConversationLanguage.ENGLISH;
  }

  @Override
  @NotNull
  public ChatMessage getKnowledgeableBotMessage(final String knowledge) {
    return new ChatMessage(ChatRole.SYSTEM, String.format(KNOWLEDGEABLE_BOT_MESSAGE, knowledge));
  }

  @Override
  @NotNull
  public ChatMessage getClosingBotMessage() {
    return CLOSING_BOT_MESSAGE;
  }

  @Override
  @NotNull
  public ChatMessage getTranslatingBotMessage(
      @NotNull final ConversationLanguage translateTo) {
    return new ChatMessage(ChatRole.SYSTEM,
        String.format(TRANSLATING_BOT_MESSAGE, this.getLanguage(), translateTo));
  }

  @Override
  @NotNull
  public ChatMessage getLanguageTranslatingBotMessage() {
    return LANGUAGE_TRANSLATING_BOT_MESSAGE;
  }
}
