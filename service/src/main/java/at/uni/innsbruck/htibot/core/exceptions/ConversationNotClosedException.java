package at.uni.innsbruck.htibot.core.exceptions;

import java.io.Serial;

public class ConversationNotClosedException extends Exception {

  @Serial
  private static final long serialVersionUID = -1341693584447674863L;

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
