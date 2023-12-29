package at.uni.innsbruck.htibot.jpa.core.business.services;

import at.uni.innsbruck.htibot.core.business.services.MessageService;
import at.uni.innsbruck.htibot.core.exceptions.PersistenceException;
import at.uni.innsbruck.htibot.core.model.conversation.Conversation;
import at.uni.innsbruck.htibot.core.model.conversation.Message;
import at.uni.innsbruck.htibot.core.model.enums.UserType;
import at.uni.innsbruck.htibot.jpa.common.services.JpaPersistenceService;
import at.uni.innsbruck.htibot.jpa.model.conversation.JpaMessage;
import at.uni.innsbruck.htibot.security.ApiKeyRestricted;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;

@ApplicationScoped
@Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = Throwable.class)
public class JpaMessageService extends JpaPersistenceService<Message, JpaMessage, Long> implements
    MessageService {

  @Serial
  private static final long serialVersionUID = -3507242399388356601L;


  @Override
  @NotNull
  @ApiKeyRestricted
  public <W extends Message> W save(final @NotNull W entity) throws PersistenceException {
    return this._save(entity);
  }

  @Override
  @NotNull
  @ApiKeyRestricted
  public <W extends Message> W update(final @NotNull W entity) throws PersistenceException {
    return this._update(entity);
  }

  @Override
  @NotNull
  @ApiKeyRestricted
  public <W extends Message> W delete(final @NotNull W entity) throws PersistenceException {
    return this._delete(entity);
  }

  @Override
  @NotNull
  protected Class<JpaMessage> getPersistenceClass() {
    return JpaMessage.class;
  }

  @Override
  @NotNull
  protected Class<Message> getInterfaceClass() {
    return Message.class;
  }

  @Override
  @ApiKeyRestricted
  @NotNull
  public Message createAndSave(@NotNull final Conversation conversation,
      @NotBlank final String message, @NotNull final UserType userType)
      throws PersistenceException {
    return this.save(new JpaMessage(conversation, message, userType));
  }
}