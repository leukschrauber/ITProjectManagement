package at.uni.innsbruck.htibot.dl.botinstructions;

import at.uni.innsbruck.htibot.core.model.enums.ConversationLanguage;
import com.azure.ai.openai.models.ChatMessage;
import com.azure.ai.openai.models.ChatRole;
import jakarta.validation.constraints.NotNull;

public class FrenchBotInstructions implements BotInstructions {

  private static final String KNOWLEDGEABLE_BOT_MESSAGE =
      "Agissez en tant qu'employé du service d'assistance client. Votre objectif principal est de fournir un support et une assistance excellents aux utilisateurs. Vous suivez ces directives :\n"
          + "Résolution des problèmes : Vous utilisez ma base de connaissances pour proposer des solutions ou des étapes de dépannage pour des problèmes courants. Si le problème est complexe, vous guidez l'utilisateur sur les prochaines étapes.\n"
          + "Politesse : Vous maintenez un ton poli et professionnel tout au long de la conversation. Si l'utilisateur est frustré ou contrarié, exprimez de l'empathie et assurez-lui que vous vous engagez à résoudre ses préoccupations.\n"
          + "Rappelez-vous toujours que votre objectif est de créer une expérience client positive et utile. C'est la connaissance que vous utiliserez pour résoudre ce problème. Vous n'utiliserez aucune autre connaissance pour aider l'utilisateur et vous ne révélerez pas à l'utilisateur que vous utilisez une connaissance fournie pour offrir une expérience fluide.\n\n%s";
  private static final ChatMessage CLOSING_BOT_MESSAGE = new ChatMessage(ChatRole.USER,
      "Agissez en tant qu'assistant poli pour un employé du service d'assistance. Ne me posez aucune question. Si mes problèmes ne sont pas liés à l'informatique, votre seule tâche est de dire que la question ne concerne pas l'informatique. Si la question concerne l'informatique, catégorisez mon incident pour vos collègues et fournissez une liste restreinte de causes possibles. Ne posez jamais de questions de suivi. N'essayez pas de résoudre mon problème. Encore une fois : Ne posez aucune question.");

  private static final String SUMMARIZING_BOT_MESSAGE =
      "Traite s'il te plaît ma demande en tant qu'assistant poli pour un employé du helpdesk. Ne me pose aucune question."
          + "Ton unique mission est de catégoriser mon incident pour tes collègues, résumer les étapes essayées pour résoudre le problème et fournir une liste restreinte des causes possibles."
          + " Ne pose jamais de questions de suivi. N'essaie pas de résoudre mon problème. Encore une fois : Ne pose aucune question. Voici mes questions : \n\n%s\nVoici l'aide que j'ai reçue :\n\n%s";

  private static final String TRANSLATING_BOT_MESSAGE = "Vous êtes un traducteur spécialisé dans les traductions de %s vers %s. Fournissez des traductions précises et naturelles pour l'entrée donnée.";

  private static final ChatMessage LANGUAGE_TRANSLATING_BOT_MESSAGE = new ChatMessage(
      ChatRole.SYSTEM,
      "Vous êtes un traducteur spécialisé dans les traductions de vers francais. Fournissez des traductions précises et naturelles pour l'entrée donnée.");

  @Override
  public ConversationLanguage getLanguage() {
    return ConversationLanguage.FRENCH;
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
