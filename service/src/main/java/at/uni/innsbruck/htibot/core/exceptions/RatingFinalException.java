package at.uni.innsbruck.htibot.core.exceptions;

public class RatingFinalException extends Exception {

  private static final long serialVersionUID = 1L;

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
