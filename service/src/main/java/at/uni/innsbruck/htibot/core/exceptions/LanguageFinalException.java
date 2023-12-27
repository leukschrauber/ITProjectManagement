package at.uni.innsbruck.htibot.core.exceptions;

import java.io.Serial;

public class LanguageFinalException extends Exception {

  @Serial
  private static final long serialVersionUID = 5687864587698552492L;

  public LanguageFinalException() {
    super();
  }

  public LanguageFinalException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public LanguageFinalException(final String message) {
    super(message);
  }

  public LanguageFinalException(final Throwable cause) {
    super(cause);
  }

}
