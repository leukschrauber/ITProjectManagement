package at.uni.innsbruck.htibot.core.business.services;

import at.uni.innsbruck.htibot.core.model.knowledge.Knowledge;
import at.uni.innsbruck.htibot.rest.generated.model.LanguageEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;

public interface ConnectorService {

  @NotBlank
  String getAnswer(final @NotBlank String prompt, final @NotNull Optional<Knowledge> knowledge, final @NotNull LanguageEnum language);

  @NotBlank
  String translate(final @NotBlank String prompt, final @NotNull LanguageEnum from, final @NotNull LanguageEnum to);
}
