package at.uni.innsbruck.htibot.core.model.conversation;

import at.uni.innsbruck.htibot.core.model.IdentityIdHolder;
import at.uni.innsbruck.htibot.core.model.enums.ConversationLanguage;
import at.uni.innsbruck.htibot.core.model.knowledge.Knowledge;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

public interface Conversation extends IdentityIdHolder {

  Optional<Boolean> getClosed();

  void setClosed(@NotNull Boolean closed);

  @NotNull
  ConversationLanguage getLanguage();

  void setLanguage(@NotNull ConversationLanguage language);

  Optional<Boolean> getRating();

  void setRating(@NotNull Boolean rating);

  @NotBlank
  String getUserId();

  void setUserId(@NotBlank String userId);

  Optional<IncidentReport> getIncidentReport();

  void setIncidentReport(@NotNull IncidentReport incidentReport);

  @NotNull
  List<Message> getMessages();

  void setMessages(@NotNull List<Message> messages);

  Optional<Knowledge> getKnowledge();

  void setKnowledge(@NotNull Knowledge knowledge);

}
