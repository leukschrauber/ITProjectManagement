package at.uni.innsbruck.htibot.security;

import at.uni.innsbruck.htibot.core.exceptions.PermissionDeniedException;
import at.uni.innsbruck.htibot.core.util.properties.ConfigProperties;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

@Priority(Interceptor.Priority.APPLICATION)
@Interceptor
@ApiKeyRestricted
public class ApiKeyRestrictedInterceptor extends
    AbstractPermissionRestrictedParameterInterceptor<ApiKeyRestricted> {

  @Inject
  private ConfigProperties configProperties;

  @Inject
  private HttpServletRequest request;

  @Override
  protected Class<ApiKeyRestricted> getPermissionAnnotationClass() {
    return ApiKeyRestricted.class;
  }

  @Override
  protected boolean hasPermission(final InvocationContext ctx, final ApiKeyRestricted annotation)
      throws PermissionDeniedException {
    return StringUtils.isBlank(this.configProperties.getProperty(ConfigProperties.HTBOT_API_KEY))
        || this.configProperties.getProperty(
        ConfigProperties.HTBOT_API_KEY).equals(this.request.getHeader("X-API-Key"));
  }

}
