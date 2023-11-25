package at.uni.innsbruck.htibot.core.exceptions;

public class ConversationClosedException extends Exception {

  private static final long serialVersionUID = 1L;

  public ConversationClosedException() {
    super();
  }

  public ConversationClosedException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public ConversationClosedException(final String message) {
    super(message);
  }

  public ConversationClosedException(final Throwable cause) {
    super(cause);
  }

}
