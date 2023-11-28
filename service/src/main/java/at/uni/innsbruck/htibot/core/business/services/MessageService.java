package at.uni.innsbruck.htibot.core.business.services;

import at.uni.innsbruck.htibot.core.exceptions.PersistenceException;
import at.uni.innsbruck.htibot.core.model.conversation.Conversation;
import at.uni.innsbruck.htibot.core.model.conversation.Message;
import at.uni.innsbruck.htibot.core.model.enums.UserType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface MessageService extends PersistenceService<Message, Long> {

  @NotNull
  Message createAndSave(@NotNull Conversation conversation, @NotBlank String message, @NotNull UserType userType)
      throws PersistenceException;

}
