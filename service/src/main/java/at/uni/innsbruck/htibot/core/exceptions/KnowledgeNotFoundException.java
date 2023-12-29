package at.uni.innsbruck.htibot.core.exceptions;

import java.io.Serial;

public class KnowledgeNotFoundException extends Exception {

  @Serial
  private static final long serialVersionUID = 1883951669351993324L;

  public KnowledgeNotFoundException() {
    super();
  }

  public KnowledgeNotFoundException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public KnowledgeNotFoundException(final String message) {
    super(message);
  }

  public KnowledgeNotFoundException(final Throwable cause) {
    super(cause);
  }

}
