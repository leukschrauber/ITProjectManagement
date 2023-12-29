package at.uni.innsbruck.htibot.core.model.knowledge;

import at.uni.innsbruck.htibot.core.model.IdentityIdHolder;
import at.uni.innsbruck.htibot.core.model.enums.UserType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface Knowledge extends IdentityIdHolder {

  @NotBlank
  String getQuestionVectorString();

  void setQuestionVectorString(@NotBlank String questionVectorString);

  void setQuestionVector(@NotNull List<Double> questionVector);

  @NotNull
  List<Double> getQuestionVector();

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

  @NotNull
  Boolean getArchived();

  void setArchived(@NotNull Boolean archived);

  Optional<String> getFilename();

  void setFilename(String filename);


}
