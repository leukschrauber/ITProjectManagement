package at.uni.innsbruck.htibot.core.exceptions;

public class PermissionDeniedException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public PermissionDeniedException() {
    super();
  }

  public PermissionDeniedException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public PermissionDeniedException(final String message) {
    super(message);
  }

  public PermissionDeniedException(final Throwable cause) {
    super(cause);
  }

}
