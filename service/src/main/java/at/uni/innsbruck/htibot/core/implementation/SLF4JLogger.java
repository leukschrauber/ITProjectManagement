package at.uni.innsbruck.htibot.core.implementation;

import at.uni.innsbruck.htibot.core.business.util.Logger;
import org.slf4j.LoggerFactory;

public class SLF4JLogger implements Logger {

  /**
   *
   */
  private static final long serialVersionUID = 9170438984316545247L;

  private final org.slf4j.Logger logger;

  public SLF4JLogger(final Class<?> clazz) {

    this.logger = LoggerFactory.getLogger(clazz);
  }

  @Override
  public boolean isTraceEnabled() {
    return this.logger.isTraceEnabled();
  }

  @Override
  public boolean isDebugEnabled() {
    return this.logger.isDebugEnabled();
  }

  @Override
  public boolean isInfoEnabled() {
    return this.logger.isInfoEnabled();
  }

  @Override
  public void error(final String message) {

    this.error(message, null);
  }

  @Override
  public void error(final Throwable e) {

    this.error(null, e);

  }

  @Override
  public void error(final String message, final Throwable e) {

    this.logger.error(message, e);

  }

  @Override
  public void warn(final Throwable e) {

    this.warn(null, e);

  }

  @Override
  public void warn(final String message) {

    this.warn(message, null);

  }

  @Override
  public void warn(final String message, final Throwable e) {

    this.logger.warn(message, e);

  }

  @Override
  public void info(final String message) {

    this.logger.info(message);

  }

  public void info(final String message, final Object... args) {
    this.logger.info(message, args);
  }

  @Override
  public void info(final String message, final Throwable e) {

    this.logger.error(message, e);

  }

  @Override
  public void debug(final String message) {

    this.logger.debug(message);

  }

  @Override
  public void debug(final String message, final Throwable e) {

    this.logger.debug(message, e);

  }

}
