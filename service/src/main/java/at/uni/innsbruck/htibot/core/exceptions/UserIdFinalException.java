package at.uni.innsbruck.htibot.core.exceptions;

import java.io.Serial;

public class UserIdFinalException extends Exception {

  @Serial
  private static final long serialVersionUID = -6013423513306632939L;

  public UserIdFinalException() {
    super();
  }

  public UserIdFinalException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public UserIdFinalException(final String message) {
    super(message);
  }

  public UserIdFinalException(final Throwable cause) {
    super(cause);
  }

}
