package at.uni.innsbruck.htibot.dl.botinstructions;

import at.uni.innsbruck.htibot.rest.generated.model.LanguageEnum;
import com.azure.ai.openai.models.ChatMessage;
import jakarta.validation.constraints.NotNull;

public interface BotInstructions {

  @NotNull LanguageEnum getLanguage();

  @NotNull ChatMessage getKnowledgeableBotMessage(String knowledge);

  @NotNull
  ChatMessage getNoClueBotMessage();

  @NotNull
  ChatMessage getClosingBotMessage();

  @NotNull
  ChatMessage getIncidentReportCreatingBotMessage();

  @NotNull
  ChatMessage getTranslatingBotMessage(
      @NotNull LanguageEnum translateTo);
}
