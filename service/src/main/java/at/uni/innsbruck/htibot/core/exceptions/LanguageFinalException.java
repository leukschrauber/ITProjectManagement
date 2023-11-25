package at.uni.innsbruck.htibot.core.exceptions;

public class LanguageFinalException extends Exception {

  private static final long serialVersionUID = 1L;

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
