package at.uni.innsbruck.htibot.rest;

import at.uni.innsbruck.htibot.core.business.services.ConnectorService;
import at.uni.innsbruck.htibot.core.business.services.ConversationService;
import at.uni.innsbruck.htibot.core.business.util.Logger;
import at.uni.innsbruck.htibot.core.exceptions.PermissionDeniedException;
import at.uni.innsbruck.htibot.rest.generated.RestResourceRoot;
import at.uni.innsbruck.htibot.rest.generated.api.HtibotApi;
import at.uni.innsbruck.htibot.rest.generated.model.BaseErrorModel;
import at.uni.innsbruck.htibot.rest.generated.model.HasOpenConversation200Response;
import at.uni.innsbruck.htibot.rest.generated.model.LanguageEnum;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.io.InputStream;
import org.apache.commons.lang3.StringUtils;

@ApplicationPath(RestResourceRoot.APPLICATION_PATH)
@ApplicationScoped
public class HTIBotApiImpl extends Application implements HtibotApi {

  @Inject
  private ConnectorService connectorService;

  @Inject
  private ConversationService conversationService;

  @Inject
  private Logger logger;

  @Override
  @NotNull
  public Response continueConversation(@NotNull final String userId) {
    return null;
  }

  @Override
  @NotNull
  public Response getAnswer(final @NotNull String prompt, final @NotNull String userId, final @NotNull LanguageEnum language) {
    if (StringUtils.isBlank(prompt)) {
      throw new IllegalArgumentException("prompt must not be null");
    }
    return Response.ok(this.connectorService.getAnswer(prompt, language)).build();
  }

  @Override
  @NotNull
  public Response hasOpenConversation(final @NotNull String userId) {
    final long startTime = System.currentTimeMillis();
    try {
      final HasOpenConversation200Response response = new HasOpenConversation200Response();
      response.setHasOpenConversation(this.conversationService.hasOpenConversation(userId));
      response.setResultCode(Status.OK.getStatusCode());
      return Response.ok(response).build();
    } catch (final PermissionDeniedException e) {
      return Response.status(Status.UNAUTHORIZED)
                     .entity(new BaseErrorModel().resultCode(Status.UNAUTHORIZED.getStatusCode()).message(e.getMessage())).build();
    } catch (final ConstraintViolationException e) {
      return Response.status(Status.BAD_REQUEST)
                     .entity(new BaseErrorModel().resultCode(Status.BAD_REQUEST.getStatusCode()).message(e.getMessage())).build();
    } catch (final Exception e) {
      return Response.status(Status.INTERNAL_SERVER_ERROR)
                     .entity(new BaseErrorModel().resultCode(Status.INTERNAL_SERVER_ERROR.getStatusCode()).message(e.getMessage())).build();
    } finally {
      this.logger.info(
          String.format("hasOpenConversation with userId %s completed in %s", userId, System.currentTimeMillis() - startTime));
    }
  }

  @Override
  @NotNull
  public Response rateConversation(final @NotNull String userId, final @NotNull Boolean rating) {
    return null;
  }

  @Override
  @NotNull
  public Response updateKnowledgeDB(final InputStream zipFileInputStream, final Boolean cleanUp) {
    if (zipFileInputStream == null) {
      throw new IllegalArgumentException("zipFileInputStream must not be null");
    }
    return null;
  }
}
