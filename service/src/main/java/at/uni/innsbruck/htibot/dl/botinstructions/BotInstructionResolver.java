package at.uni.innsbruck.htibot.dl.botinstructions;

import at.uni.innsbruck.htibot.rest.generated.model.LanguageEnum;
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
      @NotNull final LanguageEnum language) {
    return this.retrieveBotInstructions(language).getKnowledgeableBotMessage(knowledge);
  }

  @NotNull
  public ChatMessage getNoClueBotMessage(@NotNull final LanguageEnum language) {
    return this.retrieveBotInstructions(language).getNoClueBotMessage();
  }

  @NotNull
  public ChatMessage getClosingBotMessage(@NotNull final LanguageEnum language) {
    return this.retrieveBotInstructions(language).getClosingBotMessage();
  }

  @NotNull
  public ChatMessage getIncidentReportCreatingBotMessage(@NotNull final LanguageEnum language) {
    return this.retrieveBotInstructions(language).getIncidentReportCreatingBotMessage();
  }

  @NotNull
  public ChatMessage getTranslatingBotMessage(
      @NotNull final LanguageEnum translateFrom, @NotNull final LanguageEnum translateTo) {
    return this.retrieveBotInstructions(translateFrom)
        .getTranslatingBotMessage(translateTo);
  }

  private BotInstructions retrieveBotInstructions(final LanguageEnum language) {
    return this.botInstructions.stream()
        .filter(botinstruction -> botinstruction.getLanguage().equals(language)).findFirst()
        .orElseThrow();

  }
}
