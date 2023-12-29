package at.uni.innsbruck.htibot.jpa.core.business.services;

import at.uni.innsbruck.htibot.core.business.services.IncidentReportService;
import at.uni.innsbruck.htibot.core.exceptions.PersistenceException;
import at.uni.innsbruck.htibot.core.model.conversation.IncidentReport;
import at.uni.innsbruck.htibot.jpa.common.services.JpaPersistenceService;
import at.uni.innsbruck.htibot.jpa.model.conversation.JpaIncidentReport;
import at.uni.innsbruck.htibot.security.ApiKeyRestricted;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;

@ApplicationScoped
@Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = Throwable.class)
public class JpaIncidentReportService extends
    JpaPersistenceService<IncidentReport, JpaIncidentReport, Long> implements
    IncidentReportService {

  @Serial
  private static final long serialVersionUID = -3507242399388356601L;


  @Override
  @NotNull
  @ApiKeyRestricted
  public <W extends IncidentReport> W save(final @NotNull W entity) throws PersistenceException {
    return this._save(entity);
  }

  @Override
  @NotNull
  @ApiKeyRestricted
  public <W extends IncidentReport> W update(final @NotNull W entity) throws PersistenceException {
    return this._update(entity);
  }

  @Override
  @NotNull
  @ApiKeyRestricted
  public <W extends IncidentReport> W delete(final @NotNull W entity) throws PersistenceException {
    return this._delete(entity);
  }

  @Override
  @NotNull
  protected Class<JpaIncidentReport> getPersistenceClass() {
    return JpaIncidentReport.class;
  }

  @Override
  @NotNull
  protected Class<IncidentReport> getInterfaceClass() {
    return IncidentReport.class;
  }

  @Override
  @ApiKeyRestricted
  @NotNull
  public IncidentReport createAndSave(@NotBlank final String text)
      throws PersistenceException {
    return this.save(new JpaIncidentReport(text));
  }
}