package at.uni.innsbruck.htibot.jpa.model.knowledge;

import at.uni.innsbruck.htibot.core.model.enums.UserType;
import at.uni.innsbruck.htibot.core.model.knowledge.Knowledge;
import at.uni.innsbruck.htibot.core.model.knowledge.KnowledgeResource;
import at.uni.innsbruck.htibot.core.util.EmbeddingConverter;
import at.uni.innsbruck.htibot.jpa.model.JpaIdentityIdHolder;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Entity
public class JpaKnowledge extends JpaIdentityIdHolder implements Knowledge {


  @Serial
  private static final long serialVersionUID = -6783963507806025652L;

  @NotBlank
  @Column(columnDefinition = "TEXT NOT NULL")
  private String questionVector;

  @NotBlank
  @Column(columnDefinition = "TEXT NOT NULL")
  private String question;

  @Column
  private String filename;

  @NotBlank
  @Column(columnDefinition = "TEXT NOT NULL")
  private String answer;

  @Column
  private Boolean archived;

  @Enumerated(EnumType.STRING)
  @NotNull
  private UserType createdBy;

  @OneToMany(targetEntity = JpaKnowledgeResource.class,
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      mappedBy = JpaKnowledgeResource_.KNOWLEDGE)
  @NotNull
  private Set<KnowledgeResource> knowledgeResources;

  @Deprecated
  protected JpaKnowledge() {
    //needed for JPA
  }

  public JpaKnowledge(@NotBlank final String questionVector, @NotBlank final String question,
      @NotBlank final String answer,
      @NotNull final UserType createdBy, @NotNull final Boolean archived) {
    this.questionVector = questionVector;
    this.question = question;
    this.answer = answer;
    this.createdBy = createdBy;
    this.knowledgeResources = new HashSet<>();
    this.archived = archived;
  }

  @Override
  @NotBlank
  public String getQuestionVectorString() {
    return this.questionVector;
  }

  @Override
  public void setQuestionVectorString(@NotBlank final String questionVector) {
    this.questionVector = questionVector;
  }

  @Override
  @NotNull
  public List<Double> getQuestionVector() {
    return EmbeddingConverter.getAsEmbedding(this.questionVector);
  }

  @Override
  public void setQuestionVector(@NotNull final List<Double> embedding) {
    this.questionVector = EmbeddingConverter.getAsString(embedding);
  }

  @Override
  @NotBlank
  public String getQuestion() {
    return this.question;
  }

  @Override
  public void setQuestion(@NotBlank final String question) {
    this.question = question;
  }

  @Override
  @NotBlank
  public String getAnswer() {
    return this.answer;
  }

  @Override
  public void setAnswer(@NotBlank final String answer) {
    this.answer = answer;
  }

  @Override
  @NotNull
  public UserType getCreatedBy() {
    return this.createdBy;
  }

  @Override
  public void setCreatedBy(@NotNull final UserType createdBy) {
    this.createdBy = createdBy;
  }

  @Override
  public Set<KnowledgeResource> getKnowledgeResources() {
    return this.knowledgeResources;
  }

  @Override
  public void setKnowledgeResources(final Set<KnowledgeResource> knowledgeResources) {
    this.knowledgeResources = knowledgeResources;
  }

  @NotNull
  @Override
  public Boolean getArchived() {
    return this.archived;
  }

  @Override
  public void setArchived(@NotNull final Boolean archived) {
    this.archived = archived;
  }

  @Override
  public Optional<String> getFilename() {
    return Optional.ofNullable(this.filename);
  }

  @Override
  public void setFilename(final String fileName) {
    this.filename = fileName;
  }


}
