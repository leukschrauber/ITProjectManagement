package at.uni.innsbruck.htibot.core.exceptions;

import java.io.Serial;

public class MaxMessagesExceededException extends Exception {

  @Serial
  private static final long serialVersionUID = -7343600908523358070L;

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
