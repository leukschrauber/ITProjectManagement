package at.uni.innsbruck.htibot.core.exceptions;

import java.io.Serial;

public class ConversationNotFoundException extends Exception {

  @Serial
  private static final long serialVersionUID = 1883951669351993324L;

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
