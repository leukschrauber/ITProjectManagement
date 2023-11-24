package at.uni.innsbruck.htibot.core.exceptions;

public class PersistenceException extends Exception {

  private static final long serialVersionUID = -2850579037829406463L;

  public PersistenceException() {
    super();
  }

  public PersistenceException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public PersistenceException(final String message) {
    super(message);
  }

  public PersistenceException(final Throwable cause) {
    super(cause);
  }

}
