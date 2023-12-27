package at.uni.innsbruck.htibot.core.exceptions;

import java.io.Serial;

public class RatingFinalException extends Exception {

  @Serial
  private static final long serialVersionUID = -4326068724466805487L;

  public RatingFinalException() {
    super();
  }

  public RatingFinalException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public RatingFinalException(final String message) {
    super(message);
  }

  public RatingFinalException(final Throwable cause) {
    super(cause);
  }

}
