package at.uni.innsbruck.htibot.core.exceptions;

import java.io.Serial;

public class PermissionDeniedException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 5774645327725839412L;

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
