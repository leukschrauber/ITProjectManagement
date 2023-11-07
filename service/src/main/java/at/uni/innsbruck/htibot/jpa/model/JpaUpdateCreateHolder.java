package at.uni.innsbruck.htibot.jpa.model;

import at.uni.innsbruck.htibot.core.model.UpdateCreateHolder;
import jakarta.persistence.MappedSuperclass;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@MappedSuperclass
public class JpaUpdateCreateHolder implements UpdateCreateHolder {

  private static final long serialVersionUID = 555090066260376440L;

  @CreationTimestamp
  private ZonedDateTime createdAt;

  @UpdateTimestamp
  private ZonedDateTime updatedAt;

  protected JpaUpdateCreateHolder() {
    // needed for JPA
  }

  @Override
  public Optional<ZonedDateTime> getCreatedAt() {
    return Optional.ofNullable(this.createdAt);
  }

  @Override
  public Optional<ZonedDateTime> getUpdatedAt() {
    return Optional.ofNullable(this.updatedAt);
  }

}
