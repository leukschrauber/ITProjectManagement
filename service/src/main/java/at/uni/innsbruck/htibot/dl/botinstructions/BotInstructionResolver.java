package at.uni.innsbruck.htibot.dl.botinstructions;

import at.uni.innsbruck.htibot.core.model.enums.ConversationLanguage;
import com.azure.ai.openai.models.ChatMessage;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@ApplicationScoped
public class BotInstructionResolver {

  private final List<BotInstructions> botInstructions = List.of(new GermanBotInstructions(),
      new EnglishBotInstructions(), new FrenchBotInstructions(), new ItalianBotInstructions());

  @NotNull
  public ChatMessage getKnowledgeableBotMessage(@NotNull final String knowledge,
      @NotNull final ConversationLanguage language) {
    return this.retrieveBotInstructions(language).getKnowledgeableBotMessage(knowledge);
  }

  @NotNull
  public ChatMessage getClosingBotMessage(@NotNull final ConversationLanguage language) {
    return this.retrieveBotInstructions(language).getClosingBotMessage();
  }

  @NotNull
  public ChatMessage getTranslatingBotMessage(
      @NotNull final ConversationLanguage translateFrom,
      @NotNull final ConversationLanguage translateTo) {
    return this.retrieveBotInstructions(translateFrom)
        .getTranslatingBotMessage(translateTo);
  }

  @NotNull
  public ChatMessage getLanguageTranslatingBotMessage(
      @NotNull final ConversationLanguage translateTo) {
    return this.retrieveBotInstructions(translateTo)
        .getLanguageTranslatingBotMessage();
  }

  @NotNull
  public ChatMessage getSummarizingBotMessage(
      @NotNull final ConversationLanguage language, final String userQuestion, final String botAnswer) {
    return this.retrieveBotInstructions(language)
        .getSummarizingBotMessage(userQuestion, botAnswer);
  }

  private BotInstructions retrieveBotInstructions(final ConversationLanguage language) {
    return this.botInstructions.stream()
        .filter(botinstruction -> botinstruction.getLanguage().equals(language)).findFirst()
        .orElseThrow();

  }
}
