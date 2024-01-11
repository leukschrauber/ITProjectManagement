package at.uni.innsbruck.htibot.dl.botinstructions;

import at.uni.innsbruck.htibot.core.model.enums.ConversationLanguage;
import com.azure.ai.openai.models.ChatMessage;
import jakarta.validation.constraints.NotNull;

public interface BotInstructions {

  @NotNull ConversationLanguage getLanguage();

  @NotNull ChatMessage getKnowledgeableBotMessage(String knowledge);

  @NotNull
  ChatMessage getClosingBotMessage();

  @NotNull
  ChatMessage getTranslatingBotMessage(
      @NotNull ConversationLanguage translateTo);

  @NotNull
  ChatMessage getLanguageTranslatingBotMessage();
}
