package at.uni.innsbruck.htibot.core.model.conversation;

import at.uni.innsbruck.htibot.core.model.IdentityIdHolder;
import at.uni.innsbruck.htibot.core.model.enums.UserType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface Message extends IdentityIdHolder {

  @NotBlank
  String getMessage();

  void setMessage(@NotBlank String message);

  @NotNull
  UserType getCreatedBy();

  void setCreatedBy(@NotNull UserType userType);

  @NotNull
  Conversation getConversation();

  void setConversation(@NotNull Conversation conversation);

}
