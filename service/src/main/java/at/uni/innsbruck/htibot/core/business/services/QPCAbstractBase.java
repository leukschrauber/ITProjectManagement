package at.uni.innsbruck.htibot.core.business.services;

import at.uni.innsbruck.htibot.core.util.ObjectUtil;
import java.io.Serializable;
import java.util.Collection;
import org.apache.commons.collections4.CollectionUtils;

public abstract class QPCAbstractBase implements Serializable {

  private static final long serialVersionUID = 3042731179059528276L;

  private boolean dirty;

  public boolean isDirty() {
    return this.dirty;
  }

  protected <V extends QPCAbstractBase> V setDirty(final boolean dirty) {
    this.dirty = dirty;
    return (V) this;
  }

  public <V extends QPCAbstractBase> V clearDirtyFlag() {
    this.setDirty(false);
    return (V) this;
  }

  public <V extends QPCAbstractBase> V markDirty() {
    this.setDirty(true);
    return (V) this;
  }

  protected <W, C extends Collection<W>> C checkIfShouldMarkDirty(final C field, final C newValue) {
    if (ObjectUtil.areAllNull(field, newValue)) {
      return newValue;
    }

    if (ObjectUtil.isAnyNull(field, newValue)) {
      this.markDirty();
      return newValue;
    }

    if (!CollectionUtils.isEqualCollection(field, newValue)) {
      this.markDirty();
    }
    return newValue;
  }

  protected <W> W checkIfShouldMarkDirty(final W field, final W newValue) {
    if (!ObjectUtil.equalsWithNullCheck(field, newValue)) {
      this.markDirty();
    }

    return newValue;
  }

}
