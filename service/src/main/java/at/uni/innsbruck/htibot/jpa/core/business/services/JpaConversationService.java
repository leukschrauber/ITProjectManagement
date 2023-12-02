package at.uni.innsbruck.htibot.jpa.core.business.services;

import at.uni.innsbruck.htibot.core.business.services.ConversationService;
import at.uni.innsbruck.htibot.core.business.services.MessageService;
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
import at.uni.innsbruck.htibot.jpa.common.services.JpaPersistenceService;
import at.uni.innsbruck.htibot.jpa.model.conversation.JpaConversation;
import at.uni.innsbruck.htibot.jpa.model.conversation.JpaConversation_;
import at.uni.innsbruck.htibot.security.ApiKeyRestricted;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.util.Optional;
import java.util.Set;

@ApplicationScoped
@Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = Throwable.class)
public class JpaConversationService extends JpaPersistenceService<Conversation, JpaConversation, Long> implements ConversationService {

  @Serial
  private static final long serialVersionUID = -3507242399388356601L;

  @Inject
  private MessageService messageService;

  @Override
  @NotNull
  public Conversation createAndSave(final Boolean closed, @NotNull final ConversationLanguage language, final Boolean rating,
                                    @NotBlank final String userId, final IncidentReport incidentReport, @NotNull final Set<Message> messages,
                                    final Knowledge knowledge)
      throws PersistenceException {
    final Conversation conversation = new JpaConversation(language, userId);

    conversation.setClosed(closed);
    conversation.setRating(rating);
    conversation.setIncidentReport(incidentReport);
    conversation.setMessages(messages);
    conversation.setKnowledge(knowledge);

    return this.save(conversation);
  }

  @Override
  @NotNull
  public Conversation update(@NotNull final Conversation conversation, final Boolean closed,
                             @NotNull final ConversationLanguage language,
                             final Boolean rating,
                             @NotBlank final String userId, final IncidentReport incidentReport, @NotNull final Set<Message> messages, final Knowledge knowledge)
      throws PersistenceException, ConversationClosedException, LanguageFinalException, RatingFinalException {

    if (conversation.getClosed().orElse(Boolean.FALSE)) {
      throw new ConversationClosedException("Closed conversation can not be changed.");
    }

    if (!conversation.getLanguage().equals(language)) {
      throw new LanguageFinalException("Language of a Conversation can not be changed once set.");
    }

    if (rating != null && conversation.getRating().isPresent() && conversation.getRating().equals(rating)) {
      throw new RatingFinalException("Rating of a Conversation can not be changed once set.");
    }

    conversation.setClosed(closed);
    conversation.setLanguage(language);
    conversation.setRating(rating);
    conversation.setUserId(userId);
    conversation.setIncidentReport(incidentReport);
    conversation.setMessages(messages);
    conversation.setKnowledge(knowledge);

    return this.update(conversation);
  }

  @Override
  @NotNull
  public Conversation addMessage(final @NotNull Conversation conversation, final @NotBlank String message,
                                 final @NotNull UserType createdBy)
      throws PersistenceException {
    final Message messageObject = this.messageService.createAndSave(conversation, message, createdBy);
    conversation.getMessages().add(messageObject);
    return this._update(conversation);
  }


  @NotNull
  @Override
  public Conversation continueConversation(@NotBlank final String userId) throws ConversationNotFoundException {
    final Conversation conversation = this.executeSingleResultQuery(
                                        (query, cb, root) -> cb.and(cb.equal(root.get(JpaConversation_.userId), userId), cb.isFalse(root.get(JpaConversation_.CLOSED))))
                                          .orElseThrow(
                                        () -> new ConversationNotFoundException(String.format("User %s has no conversation yet.", userId)));

    conversation.setClosed(Boolean.FALSE);

    return conversation;
  }

  @Override
  @NotNull
  public Conversation rateConversation(@NotNull final Conversation conversation, final boolean rating)
      throws PersistenceException {
    conversation.setClosed(true);
    conversation.setRating(rating);
    return this._update(conversation);
  }

  @Override
  public Optional<Conversation> getByUserId(final String userId) {
    return this.executeSingleResultQuery(
        (query, cb, root) -> cb.and(cb.equal(root.get(JpaConversation_.userId), userId), cb.isFalse(root.get(JpaConversation_.CLOSED))));
  }

  @Override
  @ApiKeyRestricted
  public boolean hasOpenConversation(final @NotBlank String userId) {
    return this.executeCountQuery(
        ((query, cb, root) -> cb.and(cb.equal(root.get(JpaConversation_.userId), userId), cb.isFalse(root.get(JpaConversation_.CLOSED)))),
        true) > 0;
  }

  @Override
  @NotNull
  public <W extends Conversation> W save(final @NotNull W entity) throws PersistenceException {
    return this._save(entity);
  }

  @Override
  @NotNull
  public <W extends Conversation> W update(final @NotNull W entity) throws PersistenceException {
    return this._update(entity);
  }

  @Override
  @NotNull
  public <W extends Conversation> W delete(final @NotNull W entity) throws PersistenceException {
    return this._delete(entity);
  }

  @Override
  @NotNull
  protected Class<JpaConversation> getPersistenceClass() {
    return JpaConversation.class;
  }

  @Override
  @NotNull
  protected Class<Conversation> getInterfaceClass() {
    return Conversation.class;
  }

}