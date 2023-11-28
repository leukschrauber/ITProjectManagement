package at.uni.innsbruck.htibot.core.exceptions;

public class ConversationNotClosedException extends Exception {

  private static final long serialVersionUID = 1L;

  public ConversationNotClosedException() {
    super();
  }

  public ConversationNotClosedException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public ConversationNotClosedException(final String message) {
    super(message);
  }

  public ConversationNotClosedException(final Throwable cause) {
    super(cause);
  }

}
