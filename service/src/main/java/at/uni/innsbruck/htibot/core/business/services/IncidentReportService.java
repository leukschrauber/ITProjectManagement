package at.uni.innsbruck.htibot.core.business.services;

import at.uni.innsbruck.htibot.core.exceptions.PersistenceException;
import at.uni.innsbruck.htibot.core.model.conversation.IncidentReport;
import at.uni.innsbruck.htibot.security.ApiKeyRestricted;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface IncidentReportService extends PersistenceService<IncidentReport, Long> {

  @ApiKeyRestricted
  @NotNull
  IncidentReport createAndSave(@NotBlank String text)
      throws PersistenceException;

}
