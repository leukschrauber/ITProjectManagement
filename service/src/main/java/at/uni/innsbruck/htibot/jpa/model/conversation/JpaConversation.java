package at.uni.innsbruck.htibot.jpa.model.conversation;

import at.uni.innsbruck.htibot.core.model.conversation.Conversation;
import at.uni.innsbruck.htibot.core.model.conversation.Message;
import at.uni.innsbruck.htibot.core.model.knowledge.Knowledge;
import at.uni.innsbruck.htibot.jpa.model.JpaIdentityIdHolder;
import at.uni.innsbruck.htibot.jpa.model.JpaUpdateCreateHolder_;
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
import jakarta.persistence.OrderBy;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
public class JpaConversation extends JpaIdentityIdHolder implements Conversation {

  @Serial
  private static final long serialVersionUID = 1308078277910368033L;

  @Column
  private Boolean closed;

  @Column
  private Boolean rating;

  @NotBlank
  @Column
  private String userId;

  @OneToMany(targetEntity = JpaMessage.class,
      fetch = FetchType.LAZY,
      cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
      orphanRemoval = true,
      mappedBy = JpaMessage_.CONVERSATION)
  @OrderBy(value = JpaUpdateCreateHolder_.CREATED_AT + " ASC")
  private List<Message> messages;

  @OneToOne(targetEntity = JpaKnowledge.class,
      fetch = FetchType.LAZY,
      cascade = {CascadeType.REMOVE},
      orphanRemoval = true)
  @JoinColumn(name = "knowledge_id")
  private Knowledge knowledge;

  @Deprecated
  protected JpaConversation() {
    //needed for JPA
  }

  public JpaConversation(
      @NotBlank final String userId) {
    this.userId = userId;
    this.messages = new ArrayList<>();
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
  public List<Message> getMessages() {
    return this.messages;
  }

  @Override
  public void setMessages(final @NotNull List<Message> messages) {
    this.messages = messages;
  }

  @Override
  public Optional<Knowledge> getKnowledge() {
    return Optional.ofNullable(this.knowledge);
  }

  @Override
  public void setKnowledge(final Knowledge knowledge) {
    this.knowledge = knowledge;
  }

}
