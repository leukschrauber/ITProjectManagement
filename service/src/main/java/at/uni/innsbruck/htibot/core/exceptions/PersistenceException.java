package at.uni.innsbruck.htibot.core.exceptions;

import java.io.Serial;

public class PersistenceException extends Exception {

  @Serial
  private static final long serialVersionUID = -1965837547421677702L;

  public PersistenceException() {
    super();
  }

  public PersistenceException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public PersistenceException(final String message) {
    super(message);
  }

  public PersistenceException(final Throwable cause) {
    super(cause);
  }

}
