package at.uni.innsbruck.htibot.rest.util;

import at.uni.innsbruck.htibot.core.model.enums.ConversationLanguage;
import at.uni.innsbruck.htibot.rest.generated.model.LanguageEnum;
import jakarta.validation.constraints.NotNull;

public class RestUtil {

  public static ConversationLanguage fromConversationLanguage(
      @NotNull final LanguageEnum languageEnum) {
    switch (languageEnum) {
      case FRENCH -> {
        return ConversationLanguage.FRENCH;
      }
      case GERMAN -> {
        return ConversationLanguage.GERMAN;
      }
      case ENGLISH -> {
        return ConversationLanguage.ENGLISH;
      }
      case ITALIAN -> {
        return ConversationLanguage.ITALIAN;
      }
    }
    throw new IllegalArgumentException(
        String.format("ConversationLanguage %s not supported.", languageEnum));
  }

}
