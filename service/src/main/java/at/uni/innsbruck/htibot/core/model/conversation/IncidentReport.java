package at.uni.innsbruck.htibot.core.model.conversation;

import at.uni.innsbruck.htibot.core.model.IdentityIdHolder;
import jakarta.validation.constraints.NotBlank;

public interface IncidentReport extends IdentityIdHolder {

  @NotBlank
  String getText();

  void setText(@NotBlank String text);

}
