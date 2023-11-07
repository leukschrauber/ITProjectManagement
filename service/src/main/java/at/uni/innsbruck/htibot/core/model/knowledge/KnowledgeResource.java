package at.uni.innsbruck.htibot.core.model.knowledge;

import at.uni.innsbruck.htibot.core.model.IdentityIdHolder;
import at.uni.innsbruck.htibot.core.model.enums.UserType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface KnowledgeResource extends IdentityIdHolder {

  @NotBlank
  String getResourcePath();

  void setResourcePath(@NotBlank String resourcePath);

  @NotNull
  UserType getCreatedBy();

  void setCreatedBy(@NotNull UserType userType);

  @NotNull
  Knowledge getKnowledge();

  void setKnowledge(@NotNull Knowledge knowledge);


}
