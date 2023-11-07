package at.uni.innsbruck.htibot.core.business.util;

import java.io.Serializable;

public interface Logger extends Serializable {

  void error(final String message);

  void error(final Throwable e);

  void error(final String message, final Throwable e);

  void warn(final Throwable e);

  void warn(final String message);

  void warn(final String message, final Throwable e);

  void info(final String message);

  void info(final String message, final Throwable e);

  void debug(final String message);

  void debug(final String message, final Throwable e);

  boolean isTraceEnabled();

  boolean isDebugEnabled();

  boolean isInfoEnabled();

}
