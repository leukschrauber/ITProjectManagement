package at.uni.innsbruck.htibot.rest;

import at.uni.innsbruck.htibot.core.business.services.ConnectorService;
import at.uni.innsbruck.htibot.core.business.services.ConversationService;
import at.uni.innsbruck.htibot.core.business.services.InputClassifierService;
import at.uni.innsbruck.htibot.core.business.services.KnowledgeService;
import at.uni.innsbruck.htibot.core.business.util.Logger;
import at.uni.innsbruck.htibot.core.exceptions.ConversationNotClosedException;
import at.uni.innsbruck.htibot.core.exceptions.ConversationNotFoundException;
import at.uni.innsbruck.htibot.core.exceptions.PermissionDeniedException;
import at.uni.innsbruck.htibot.core.model.conversation.Conversation;
import at.uni.innsbruck.htibot.core.model.enums.UserType;
import at.uni.innsbruck.htibot.core.model.knowledge.Knowledge;
import at.uni.innsbruck.htibot.core.util.ExceptionalSupplier;
import at.uni.innsbruck.htibot.rest.generated.RestResourceRoot;
import at.uni.innsbruck.htibot.rest.generated.api.HtibotApi;
import at.uni.innsbruck.htibot.rest.generated.model.BaseErrorModel;
import at.uni.innsbruck.htibot.rest.generated.model.GetAnswer200Response;
import at.uni.innsbruck.htibot.rest.generated.model.HasOpenConversation200Response;
import at.uni.innsbruck.htibot.rest.generated.model.LanguageEnum;
import at.uni.innsbruck.htibot.rest.util.RestUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

@ApplicationPath(RestResourceRoot.APPLICATION_PATH)
@ApplicationScoped
public class HTIBotApiImpl extends Application implements HtibotApi {

  @Inject
  private ConnectorService connectorService;

  @Inject
  private ConversationService conversationService;

  @Inject
  private InputClassifierService inputClassifierService;

  @Inject
  private KnowledgeService knowledgeService;

  @Inject
  private Logger logger;

  @Override
  @NotNull
  public Response continueConversation(@NotNull final String userId) {
    return this.runWithinTryCatch("continueConversation", () -> {
      this.conversationService.continueConversation(userId);
      return Response.ok().build();
    });
  }

  @Override
  @NotNull
  public Response getAnswer(@NotNull final String prompt, final @NotNull String userId, final @NotNull LanguageEnum language) {
    return this.runWithinTryCatch("getAnswer", () -> {
      if (StringUtils.isBlank(prompt)) {
        throw new IllegalArgumentException("prompt must not be blank");
      }

      if (this.conversationService.hasOpenConversation(userId)) {
        throw new ConversationNotClosedException(
            "User can not conversate in Conversation that has not been closed or been requested for further conversation.");
      }

      final Optional<Conversation> conversationOptional = this.conversationService.getByUserId(userId);

      String englishPrompt = "";

      if (!LanguageEnum.ENGLISH.equals(language)) {
        englishPrompt = this.connectorService.translate(prompt, language, LanguageEnum.ENGLISH);
      } else {
        englishPrompt = prompt;
      }

      final String questionVector = this.inputClassifierService.retrieveQuestionVector(englishPrompt);
      final Optional<Knowledge> knowledge = this.knowledgeService.retrieveKnowledge(questionVector);
      final String answer = this.connectorService.getAnswer(englishPrompt, knowledge, language);

      Conversation conversation = null;

      if (conversationOptional.isEmpty()) {
        conversation = this.conversationService.createAndSave(questionVector, false, RestUtil.fromLanguageEnum(language), null, userId,
                                                              null,
                                                              new HashSet<>(), knowledge.orElse(null));
      } else {
        conversation = conversationOptional.orElseThrow();
      }

      this.conversationService.addMessage(conversation, prompt, UserType.USER);
      this.conversationService.addMessage(conversation, answer, UserType.SYSTEM);

      return Response.ok(new GetAnswer200Response().answer(answer).resultCode(Status.OK.getStatusCode())).build();
    });
  }

  @Override
  @NotNull
  public Response hasOpenConversation(final @NotNull String userId) {

    return this.runWithinTryCatch("hasOpenConversation", () -> {
      final HasOpenConversation200Response response = new HasOpenConversation200Response();
      response.setHasOpenConversation(this.conversationService.hasOpenConversation(userId));
      response.setResultCode(Status.OK.getStatusCode());
      return Response.ok(response).build();
    });
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

  private Response runWithinTryCatch(final String operationId, final ExceptionalSupplier<Response> supplier) {
    final long startTime = System.currentTimeMillis();
    try {
      return supplier.get();
    } catch (final PermissionDeniedException e) {
      this.logger.error(String.format("Operation %s failed with %s", operationId, e.getClass().getName()), e);
      return Response.status(Status.UNAUTHORIZED).build();
    } catch (final ConstraintViolationException | IllegalArgumentException e) {
      this.logger.error(String.format("Operation %s failed with %s", operationId, e.getClass().getName()), e);
      return Response.status(Status.BAD_REQUEST)
                     .entity(new BaseErrorModel().resultCode(Status.BAD_REQUEST.getStatusCode()).message(e.getMessage())).build();
    } catch (final ConversationNotClosedException e) {
      this.logger.error(String.format("Operation %s failed with %s", operationId, e.getClass().getName()), e);
      return Response.status(Status.CONFLICT)
                     .entity(new BaseErrorModel().resultCode(Status.CONFLICT.getStatusCode()).message(e.getMessage())).build();
    } catch (final ConversationNotFoundException e) {
      this.logger.error(String.format("Operation %s failed with %s", operationId, e.getClass().getName()), e);
      return Response.status(Status.NOT_FOUND)
                     .entity(new BaseErrorModel().resultCode(Status.NOT_FOUND.getStatusCode()).message(e.getMessage())).build();
    } catch (final Exception e) {
      this.logger.error(String.format("Operation %s failed with %s", operationId, e.getClass().getName()), e);
      return Response.status(Status.INTERNAL_SERVER_ERROR)
                     .entity(new BaseErrorModel().resultCode(Status.INTERNAL_SERVER_ERROR.getStatusCode()).message(e.getMessage())).build();

    } finally {
      this.logger.info(
          String.format("%s completed in %s ms", operationId, System.currentTimeMillis() - startTime));
    }
  }
}
