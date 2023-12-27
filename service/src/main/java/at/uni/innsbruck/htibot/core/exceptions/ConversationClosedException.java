package at.uni.innsbruck.htibot.core.exceptions;

import java.io.Serial;

public class ConversationClosedException extends Exception {

  @Serial
  private static final long serialVersionUID = 4470987352142325088L;

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
