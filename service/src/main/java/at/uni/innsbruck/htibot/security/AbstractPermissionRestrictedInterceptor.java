package at.uni.innsbruck.htibot.security;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import java.lang.annotation.Annotation;

public abstract class AbstractPermissionRestrictedInterceptor<T extends Annotation> {


  @AroundInvoke
  public Object aroundInvoke(final InvocationContext ctx) throws Exception {
    return this.checkPermissionAndProceed(ctx);
  }

  protected T getAnnotation(final InvocationContext ctx) {
    return ctx.getMethod().getAnnotation(this.getPermissionAnnotationClass());
  }

  protected abstract Object checkPermissionAndProceed(InvocationContext ctx) throws Exception;

  protected abstract Class<T> getPermissionAnnotationClass();

}
