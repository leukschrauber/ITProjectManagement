package at.uni.innsbruck.htibot.security;

import at.uni.innsbruck.htibot.core.exceptions.PermissionDeniedException;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import java.lang.annotation.Annotation;

public abstract class AbstractPermissionRestrictedParameterInterceptor<T extends Annotation> extends
                                                                                             AbstractPermissionRestrictedInterceptor<T> {

  @Override
  @AroundInvoke
  protected final Object checkPermissionAndProceed(final InvocationContext ctx) throws Exception {
    if (!this.hasPermission(ctx, this.getAnnotation(ctx))) {
      throw new PermissionDeniedException();
    }

    return ctx.proceed();
  }

  @Override
  protected abstract Class<T> getPermissionAnnotationClass();

  protected abstract boolean hasPermission(InvocationContext ctx, T annotation) throws Exception;

}
