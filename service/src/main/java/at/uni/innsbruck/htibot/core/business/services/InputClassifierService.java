package at.uni.innsbruck.htibot.core.business.services;

import jakarta.validation.constraints.NotBlank;

public interface InputClassifierService {

  @NotBlank
  String retrieveQuestionVector(@NotBlank String question);
}
