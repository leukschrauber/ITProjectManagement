package at.uni.innsbruck.htibot.jpa.model;

import at.uni.innsbruck.htibot.core.model.IdentityIdHolder;
import at.uni.innsbruck.htibot.core.util.ObjectUtil;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class JpaIdentityIdHolder extends JpaUpdateCreateHolder implements IdentityIdHolder {

  private static final long serialVersionUID = 2388091724587395667L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Override
  public Long getId() {
    return this.id;
  }

  @Override
  public int hashCode() {
    return ObjectUtil.hashCode(this, JpaIdentityIdHolder::getId);
  }

  @Override
  public boolean equals(final Object obj) {
    return ObjectUtil.equalsWithNullCheck(this, obj, JpaIdentityIdHolder::getId);
  }

}
