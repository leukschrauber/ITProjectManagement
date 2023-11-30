package at.uni.innsbruck.htibot.core.exceptions;

public class MaxMessagesExceededException extends Exception {

  private static final long serialVersionUID = 1L;

  public MaxMessagesExceededException() {
    super();
  }

  public MaxMessagesExceededException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public MaxMessagesExceededException(final String message) {
    super(message);
  }

  public MaxMessagesExceededException(final Throwable cause) {
    super(cause);
  }

}
