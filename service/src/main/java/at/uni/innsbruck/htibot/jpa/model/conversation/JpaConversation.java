package at.uni.innsbruck.htibot.jpa.model.conversation;

import at.uni.innsbruck.htibot.core.model.conversation.Conversation;
import at.uni.innsbruck.htibot.core.model.conversation.IncidentReport;
import at.uni.innsbruck.htibot.core.model.conversation.Message;
import at.uni.innsbruck.htibot.core.model.enums.ConversationLanguage;
import at.uni.innsbruck.htibot.core.model.knowledge.Knowledge;
import at.uni.innsbruck.htibot.jpa.model.JpaIdentityIdHolder;
import at.uni.innsbruck.htibot.jpa.model.knowledge.JpaKnowledge;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Entity
public class JpaConversation extends JpaIdentityIdHolder implements Conversation {

  @Serial
  private static final long serialVersionUID = 1308078277910368033L;
  @NotBlank
  @Column
  private String questionVector;

  @Column
  private Boolean closed;

  @Enumerated(EnumType.STRING)
  @NotNull
  private ConversationLanguage language;

  @Column
  private Boolean rating;

  @NotBlank
  @Column
  private String userId;

  @OneToOne(targetEntity = JpaIncidentReport.class,
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            orphanRemoval = true)
  @JoinColumn(name = "incident_report_id")
  private IncidentReport incidentReport;

  @OneToMany(targetEntity = JpaMessage.class,
             fetch = FetchType.LAZY,
             cascade = CascadeType.ALL,
             orphanRemoval = true,
             mappedBy = JpaMessage_.CONVERSATION)
  private Set<Message> messages;

  @OneToOne(targetEntity = JpaKnowledge.class,
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            orphanRemoval = true)
  @JoinColumn(name = "knowledge_id")
  private Knowledge knowledge;

  @Deprecated
  protected JpaConversation() {
    //needed for JPA
  }

  public JpaConversation(@NotBlank final String questionVector, @NotNull final ConversationLanguage language,
                         @NotBlank final String userId) {
    this.questionVector = questionVector;
    this.language = language;
    this.userId = userId;
    this.messages = new HashSet<>();
  }

  @Override
  @NotBlank
  public String getQuestionVector() {
    return this.questionVector;
  }

  @Override
  public void setQuestionVector(@NotBlank final String questionVector) {
    this.questionVector = questionVector;
  }

  @Override
  public Optional<Boolean> getClosed() {
    return Optional.ofNullable(this.closed);
  }

  @Override
  public void setClosed(@NotNull final Boolean closed) {
    this.closed = closed;
  }

  @Override
  @NotNull
  public ConversationLanguage getLanguage() {
    return this.language;
  }

  @Override
  public void setLanguage(@NotNull final ConversationLanguage language) {
    this.language = language;
  }

  @Override
  public Optional<Boolean> getRating() {
    return Optional.ofNullable(this.rating);
  }

  @Override
  public void setRating(@NotNull final Boolean rating) {
    this.rating = rating;
  }

  @Override
  @NotBlank
  public String getUserId() {
    return this.userId;
  }

  @Override
  public void setUserId(@NotBlank final String userId) {
    this.userId = userId;
  }

  @Override
  public Optional<IncidentReport> getIncidentReport() {
    return Optional.ofNullable(this.incidentReport);
  }

  @Override
  public void setIncidentReport(final @NotNull IncidentReport incidentReport) {
    this.incidentReport = incidentReport;
  }

  @Override
  public Set<Message> getMessages() {
    return this.messages;
  }

  @Override
  public void setMessages(final @NotNull Set<Message> messages) {
    this.messages = messages;
  }

  @Override
  public Knowledge getKnowledge() {
    return this.knowledge;
  }

  @Override
  public void setKnowledge(@NotNull final Knowledge knowledge) {
    this.knowledge = knowledge;
  }

}
