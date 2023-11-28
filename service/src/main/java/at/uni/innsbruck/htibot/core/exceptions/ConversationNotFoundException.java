package at.uni.innsbruck.htibot.core.exceptions;

public class ConversationNotFoundException extends Exception {

  private static final long serialVersionUID = 1L;

  public ConversationNotFoundException() {
    super();
  }

  public ConversationNotFoundException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public ConversationNotFoundException(final String message) {
    super(message);
  }

  public ConversationNotFoundException(final Throwable cause) {
    super(cause);
  }

}
