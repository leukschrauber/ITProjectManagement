package at.uni.innsbruck.htibot.core.business.services;

import at.uni.innsbruck.htibot.core.exceptions.ConversationClosedException;
import at.uni.innsbruck.htibot.core.exceptions.ConversationNotFoundException;
import at.uni.innsbruck.htibot.core.exceptions.LanguageFinalException;
import at.uni.innsbruck.htibot.core.exceptions.PersistenceException;
import at.uni.innsbruck.htibot.core.exceptions.RatingFinalException;
import at.uni.innsbruck.htibot.core.model.conversation.Conversation;
import at.uni.innsbruck.htibot.core.model.conversation.IncidentReport;
import at.uni.innsbruck.htibot.core.model.conversation.Message;
import at.uni.innsbruck.htibot.core.model.enums.ConversationLanguage;
import at.uni.innsbruck.htibot.core.model.enums.UserType;
import at.uni.innsbruck.htibot.core.model.knowledge.Knowledge;
import at.uni.innsbruck.htibot.security.ApiKeyRestricted;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import java.util.Set;

public interface ConversationService extends PersistenceService<Conversation, Long> {


  @NotNull
  @ApiKeyRestricted
  Conversation createAndSave(Boolean closed, @NotNull ConversationLanguage language, Boolean rating,
      @NotBlank String userId, IncidentReport incidentReport, @NotNull Set<Message> messages,
      Knowledge knowledge)
      throws PersistenceException;

  @NotNull
  @ApiKeyRestricted
  Conversation update(@NotNull Conversation conversation, Boolean closed,
      @NotNull ConversationLanguage language,
      Boolean rating,
      @NotBlank String userId, IncidentReport incidentReport, @NotNull Set<Message> messages,
      Knowledge knowledge)
      throws PersistenceException, ConversationClosedException, LanguageFinalException, RatingFinalException;

  @NotNull
  @ApiKeyRestricted
  Conversation addMessage(@NotNull Conversation conversation, @NotBlank String message,
      @NotNull UserType createdBy)
      throws PersistenceException;

  @ApiKeyRestricted
  boolean hasOpenConversation(@NotBlank String userId);

  @NotNull
  @ApiKeyRestricted
  Conversation continueConversation(@NotBlank String userId) throws ConversationNotFoundException;

  @NotNull
  @ApiKeyRestricted
  Conversation rateConversation(@NotNull String userId, boolean rating)
      throws PersistenceException, ConversationNotFoundException, ConversationClosedException;

  @NotNull
  @ApiKeyRestricted
  Conversation rateConversation(@NotNull Conversation conversation, boolean rating)
      throws PersistenceException;

  @ApiKeyRestricted
  Optional<Conversation> getOpenConversationByUserId(@NotBlank String userId);

}
