package at.uni.innsbruck.htibot.dl;

import at.uni.innsbruck.htibot.core.business.services.InputClassifierService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.constraints.NotBlank;

@ApplicationScoped
public class DLInputClassifierService implements InputClassifierService {

  @Override
  @NotBlank
  public String retrieveQuestionVector(final @NotBlank String question) {
    return "0, 1, 2, 3, 4, 5, 6, 7, 8, 9";
  }
}