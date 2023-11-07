package at.uni.innsbruck.htibot.core.model.knowledge;

import at.uni.innsbruck.htibot.core.model.IdentityIdHolder;
import at.uni.innsbruck.htibot.core.model.enums.UserType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

public interface Knowledge extends IdentityIdHolder {

  @NotBlank
  String getQuestionVector();

  void setQuestionVector(@NotBlank String questionVector);

  @NotBlank
  String getQuestion();

  void setQuestion(@NotBlank String question);

  @NotBlank
  String getAnswer();

  void setAnswer(@NotBlank String answer);

  @NotNull
  UserType getCreatedBy();

  void setCreatedBy(@NotNull UserType userType);

  @NotNull
  Set<KnowledgeResource> getKnowledgeResources();

  void setKnowledgeResources(@NotNull Set<KnowledgeResource> knowledgeResources);


}
