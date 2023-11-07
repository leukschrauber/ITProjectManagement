package at.uni.innsbruck.htibot.core.model;

import java.io.Serializable;

public interface IdHolder<T extends Comparable<T>> extends Serializable {

  T getId();
}
