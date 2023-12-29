package at.uni.innsbruck.htibot.jpa.model.conversation;

import at.uni.innsbruck.htibot.core.model.conversation.IncidentReport;
import at.uni.innsbruck.htibot.jpa.model.JpaIdentityIdHolder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import java.io.Serial;

@Entity
public class JpaIncidentReport extends JpaIdentityIdHolder implements IncidentReport {


  @Serial
  private static final long serialVersionUID = -6783963507806025652L;

  @NotBlank
  @Column(columnDefinition = "TEXT NULL")
  private String text;

  public JpaIncidentReport(@NotBlank final String text) {
    this.text = text;
  }

  @Deprecated
  protected JpaIncidentReport() {
    //needed for JPA
  }

  @Override
  @NotBlank
  public String getText() {
    return this.text;
  }

  @Override
  public void setText(@NotBlank final String text) {
    this.text = text;
  }


}
