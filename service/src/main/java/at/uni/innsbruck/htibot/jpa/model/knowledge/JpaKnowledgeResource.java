package at.uni.innsbruck.htibot.jpa.model.knowledge;

import at.uni.innsbruck.htibot.core.model.enums.UserType;
import at.uni.innsbruck.htibot.core.model.knowledge.Knowledge;
import at.uni.innsbruck.htibot.core.model.knowledge.KnowledgeResource;
import at.uni.innsbruck.htibot.jpa.model.JpaIdentityIdHolder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;

@Entity
public class JpaKnowledgeResource extends JpaIdentityIdHolder implements KnowledgeResource {

  @Serial
  private static final long serialVersionUID = -6783963507806025652L;

  @NotBlank
  @Column
  private String resourcePath;

  @Enumerated(EnumType.STRING)
  @NotNull
  private UserType createdBy;

  @ManyToOne(targetEntity = JpaKnowledge.class, fetch = FetchType.LAZY, optional = false)
  @NotNull
  private Knowledge knowledge;

  @Deprecated
  protected JpaKnowledgeResource() {
    //needed for JPA
  }

  public JpaKnowledgeResource(@NotBlank final String resourcePath, @NotNull final UserType createdBy) {
    this.resourcePath = resourcePath;
    this.createdBy = createdBy;
  }

  @Override
  @NotBlank
  public String getResourcePath() {
    return this.resourcePath;
  }

  @Override
  public void setResourcePath(@NotBlank final String resourcePath) {
    this.resourcePath = resourcePath;
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
  public Knowledge getKnowledge() {
    return this.knowledge;
  }

  @Override
  public void setKnowledge(final Knowledge knowledge) {
    this.knowledge = knowledge;
  }


}
