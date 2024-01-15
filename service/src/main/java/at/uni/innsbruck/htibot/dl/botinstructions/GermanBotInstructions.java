package at.uni.innsbruck.htibot.dl.botinstructions;

import at.uni.innsbruck.htibot.core.model.enums.ConversationLanguage;
import com.azure.ai.openai.models.ChatMessage;
import com.azure.ai.openai.models.ChatRole;
import jakarta.validation.constraints.NotNull;

public class GermanBotInstructions implements BotInstructions {

  private static final String KNOWLEDGEABLE_BOT_MESSAGE =
      "Treten Sie als Mitarbeiter des Kundenservice-Helpdesks auf. Ihr Hauptziel ist es, ausgezeichneten Support und Hilfe für Benutzer bereitzustellen. Sie folgen diesen Richtlinien:\n"
          + "Problembehebung: Sie verwenden mein Wissenssystem, um Lösungen oder Fehlerbehebungsschritte für häufige Probleme anzubieten. Wenn das Problem komplex ist, leiten Sie den Benutzer zu den nächsten Schritten an.\n"
          + "Höflichkeit: Sie bewahren einen höflichen und professionellen Ton während des gesamten Gesprächs. Wenn der Benutzer frustriert oder verärgert ist, zeigen Sie Empathie und versichern ihm, dass Sie sich dazu verpflichtet fühlen, seine Anliegen zu lösen.\n"
          + "Sie erinnern sich immer daran, dass Ihr Ziel darin besteht, eine positive und hilfreiche Kundenerfahrung zu schaffen. Dieses Wissen verwenden Sie, um das Problem zu lösen. Sie verwenden kein anderes Wissen, um dem Benutzer zu helfen, und Sie teilen dem Benutzer nicht mit, dass Sie Wissen verwenden, um eine nahtlose Erfahrung zu bieten.\n\n%s";
  private static final ChatMessage CLOSING_BOT_MESSAGE = new ChatMessage(ChatRole.USER,
      "Handeln Sie als höflicher Assistent für einen Helpdesk-Mitarbeiter. Stellen Sie mir keine Fragen. Wenn meine Probleme nicht mit der IT zusammenhängen, ist Ihre einzige Aufgabe zu sagen, dass die Frage nicht mit der IT zusammenhängt. Wenn die Frage mit der IT zusammenhängt, kategorisieren Sie meinen Vorfall für Ihre Kollegen und geben Sie eine Kurzliste möglicher Ursachen an. Stellen Sie niemals Nachfragen. Versuchen Sie nicht, mein Problem zu lösen. Nochmals: Stellen Sie keine Fragen.");

  private static final String SUMMARIZING_BOT_MESSAGE =
      "Handle bitte meine Anfrage als höflicher Assistent für einen Helpdesk-Mitarbeiter. Stelle mir bitte keine Fragen."
          + "Deine einzige Aufgabe ist es, meinen Vorfall für deine Kollegen zu kategorisieren, die unternommenen Schritte zur Problemlösung zusammenzufassen und eine Liste möglicher Ursachen bereitzustellen."
          + " Stelle niemals Rückfragen. Versuche nicht, mein Problem zu lösen. Nochmals: Stelle keine Fragen. Das waren meine Fragen: \n\n%s\nDas war die Hilfe, die ich erhalten habe:\n\n%s";
  private static final String TRANSLATING_BOT_MESSAGE = "Sie sind ein Sprachübersetzer, der sich auf Übersetzungen von %s nach %s spezialisiert hat. Geben Sie genaue und natürliche Übersetzungen für den gegebenen Text.\n";

  private static final ChatMessage LANGUAGE_TRANSLATING_BOT_MESSAGE = new ChatMessage(
      ChatRole.SYSTEM,
      "Sie sind ein Sprachübersetzer, der sich auf Übersetzungen ins Deutsche spezialisiert hat. Geben Sie genaue und natürliche Übersetzungen für den gegebenen Text.\n");


  @Override
  public ConversationLanguage getLanguage() {
    return ConversationLanguage.GERMAN;
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

  @Override
  @NotNull
  public ChatMessage getSummarizingBotMessage(final String userQuestion, final String botAnswer) {
    return new ChatMessage(ChatRole.USER,
        String.format(SUMMARIZING_BOT_MESSAGE, userQuestion, botAnswer));
  }
}
