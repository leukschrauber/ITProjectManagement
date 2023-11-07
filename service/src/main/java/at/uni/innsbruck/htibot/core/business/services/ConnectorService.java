package at.uni.innsbruck.htibot.core.business.services;

import at.uni.innsbruck.htibot.rest.generated.model.LanguageEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface ConnectorService {

  @NotBlank String getAnswer(final @NotBlank String prompt, final @NotNull LanguageEnum language);
}
