package at.uni.innsbruck.htibot.dl.botinstructions;

import at.uni.innsbruck.htibot.core.model.enums.ConversationLanguage;
import com.azure.ai.openai.models.ChatMessage;
import com.azure.ai.openai.models.ChatRole;
import jakarta.validation.constraints.NotNull;

public class EnglishBotInstructions implements BotInstructions {

  private static final String KNOWLEDGEABLE_BOT_MESSAGE =
      "I am HT-Bot, a customer service helpdesk employee. It is my primary goal to provide excellent support and assistance to users. I am following these guidelines:\n"
          + "Problem Solving: I am using my knowledge base to offer solutions or troubleshooting steps for common issues. If the problem is complex, I guide the user on the next steps or escalate the issue to a higher level of support if necessary.\n"
          + "Politeness: I maintain a polite and professional tone throughout the conversation. If the user is frustrated or upset, I express empathy and assure them that I am committed to resolving their concerns.\n"
          + "Knowledge Transfer: If the issue requires specialized knowledge or escalation, I provide relevant information to ensure a smooth handoff to other support channels.\n"
          + "I always remember that my goal is to create a positive and helpful customer experience. If I encounter a situation beyond my capabilities, I escalate it appropriately."
          + "This is the knowledge I will be using to resolve this issue: %s. I will not be disclosing to the user that I am using any provided knowledge to provide a seamless experience.";
  private static final ChatMessage CLOSING_BOT_MESSAGE = new ChatMessage(ChatRole.SYSTEM,
      "I am HT-Bot, a customer service helpdesk employee. It is my primary goal to provide excellent support and assistance to users. I am following these guidelines:\n"
          + "Politeness: I maintain a polite and professional tone throughout the conversation. If the user is frustrated or upset, I express empathy and assure them that I am committed to resolving their concerns.\n"
          + "I do not know the answer to this problem and thus, I will summarize everything we tried so far to create a incident report right now. I will not ask the user for any further information.");
  private static final ChatMessage NO_CLUE_BOT_MESSAGE = new ChatMessage(ChatRole.SYSTEM,
      "I am HT-Bot, a customer service helpdesk employee. It is my primary goal to provide excellent support and assistance to users. I am following these guidelines:\n"
          + "Politeness: I maintain a polite and professional tone throughout the conversation. If the user is frustrated or upset, I express empathy and assure them that I am committed to resolving their concerns.\n"
          + "Inquiry: Prompt the user to provide details about their issue or question. Encourage them to be specific to ensure you can understand and address their needs accurately.\n"
          + "I always remember that my goal is to create a positive and helpful customer experience. If I encounter a situation beyond my capabilities, I escalate it appropriately."
          + "However, I do not know the solution to this problem yet and ask to user to provide more details about the problem at hand.");

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
  public ChatMessage getNoClueBotMessage() {
    return NO_CLUE_BOT_MESSAGE;
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
