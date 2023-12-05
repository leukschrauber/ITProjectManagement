package at.uni.innsbruck.htibot.core.business.services;

import at.uni.innsbruck.htibot.security.ApiKeyRestricted;
import jakarta.validation.constraints.NotBlank;

public interface InputClassifierService {

  @NotBlank
  @ApiKeyRestricted
  String retrieveQuestionVector(@NotBlank String question);
}
