package at.uni.innsbruck.htibot.dl.botinstructions;

import at.uni.innsbruck.htibot.core.model.enums.ConversationLanguage;
import com.azure.ai.openai.models.ChatMessage;
import com.azure.ai.openai.models.ChatRole;
import jakarta.validation.constraints.NotNull;

public class ItalianBotInstructions implements BotInstructions {

  private static final String KNOWLEDGEABLE_BOT_MESSAGE =
      "Agisci come un dipendente del servizio di assistenza clienti. Il tuo obiettivo principale è fornire un supporto eccellente e assistenza agli utenti. Segui queste linee guida:\n"
          + "Risoluzione dei problemi: Utilizzi la mia base di conoscenze per offrire soluzioni o passaggi di risoluzione dei problemi per problemi comuni. Se il problema è complesso, guidi l'utente nei passaggi successivi.\n"
          + "Cortesia: Mantieni un tono cortese e professionale durante l'intera conversazione. Se l'utente è frustrato o arrabbiato, esprimi empatia e assicuragli che sei impegnato a risolvere le sue preoccupazioni.\n"
          + "Ricordi sempre che il tuo obiettivo è creare un'esperienza positiva e utile per il cliente. Questa è la conoscenza che userai per risolvere il problema. Non utilizzerai altre conoscenze per aiutare l'utente e non informerai l'utente che stai utilizzando alcuna conoscenza fornita per offrire un'esperienza senza interruzioni.\n\n%s";
  private static final ChatMessage CLOSING_BOT_MESSAGE = new ChatMessage(ChatRole.USER,
      "Agisci come un assistente cortese per un dipendente del servizio di assistenza. Non farmi domande. Se i miei problemi non sono legati all'IT, il tuo unico compito è dire che la domanda non riguarda l'IT. Se la domanda è legata all'IT, categorizzi l'incidente per i tuoi colleghi e fornisci un elenco ristretto di possibili cause. Non fare domande di approfondimento. Non cercare di risolvere il mio problema. Di nuovo: Non fare alcuna domanda.");

  private static final String TRANSLATING_BOT_MESSAGE = "Sei un traduttore specializzato nelle traduzioni da %s a %s. Fornisci traduzioni accurate e naturali per l'input fornito.";

  private static final ChatMessage LANGUAGE_TRANSLATING_BOT_MESSAGE = new ChatMessage(
      ChatRole.SYSTEM,
      "Sei un traduttore specializzato nelle traduzioni da a italiano. Fornisci traduzioni accurate e naturali per l'input fornito.");


  @Override
  public ConversationLanguage getLanguage() {
    return ConversationLanguage.ITALIAN;
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
