package at.uni.innsbruck.htibot.jpa.model.conversation;

import at.uni.innsbruck.htibot.core.model.conversation.Conversation;
import at.uni.innsbruck.htibot.core.model.conversation.Message;
import at.uni.innsbruck.htibot.core.model.enums.UserType;
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
public class JpaMessage extends JpaIdentityIdHolder implements Message {


  @Serial
  private static final long serialVersionUID = -6783963507806025652L;

  @NotBlank
  @Column
  private String message;

  @Enumerated(EnumType.STRING)
  @NotNull
  private UserType createdBy;

  @ManyToOne(targetEntity = JpaConversation.class, fetch = FetchType.LAZY, optional = false)
  @NotNull
  private Conversation conversation;

  @Deprecated
  protected JpaMessage() {
    //needed for JPA
  }

  public JpaMessage(@NotNull final Conversation conversation, @NotBlank final String message, @NotNull final UserType createdBy) {
    this.message = message;
    this.createdBy = createdBy;
    this.conversation = conversation;
  }

  @Override
  @NotBlank
  public String getMessage() {
    return this.message;
  }

  @Override
  public void setMessage(@NotBlank final String message) {
    this.message = message;
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
  public Conversation getConversation() {
    return this.conversation;
  }

  @Override
  public void setConversation(final Conversation conversation) {
    this.conversation = conversation;
  }


}
