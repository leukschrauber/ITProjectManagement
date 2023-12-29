package at.uni.innsbruck.htibot.rest;

import at.uni.innsbruck.htibot.core.business.services.ConnectorService;
import at.uni.innsbruck.htibot.core.business.services.ConversationService;
import at.uni.innsbruck.htibot.core.business.services.IncidentReportService;
import at.uni.innsbruck.htibot.core.business.services.KnowledgeService;
import at.uni.innsbruck.htibot.core.business.util.Logger;
import at.uni.innsbruck.htibot.core.exceptions.ConversationClosedException;
import at.uni.innsbruck.htibot.core.exceptions.ConversationNotClosedException;
import at.uni.innsbruck.htibot.core.exceptions.ConversationNotFoundException;
import at.uni.innsbruck.htibot.core.exceptions.LanguageFinalException;
import at.uni.innsbruck.htibot.core.exceptions.PermissionDeniedException;
import at.uni.innsbruck.htibot.core.exceptions.UserIdFinalException;
import at.uni.innsbruck.htibot.core.model.conversation.Conversation;
import at.uni.innsbruck.htibot.core.model.conversation.IncidentReport;
import at.uni.innsbruck.htibot.core.model.enums.ConversationLanguage;
import at.uni.innsbruck.htibot.core.model.enums.UserType;
import at.uni.innsbruck.htibot.core.model.knowledge.Knowledge;
import at.uni.innsbruck.htibot.core.util.ExceptionalSupplier;
import at.uni.innsbruck.htibot.core.util.properties.ConfigProperties;
import at.uni.innsbruck.htibot.rest.generated.RestResourceRoot;
import at.uni.innsbruck.htibot.rest.generated.api.HtibotApi;
import at.uni.innsbruck.htibot.rest.generated.model.BaseErrorModel;
import at.uni.innsbruck.htibot.rest.generated.model.BaseSuccessModel;
import at.uni.innsbruck.htibot.rest.generated.model.GetAnswer200Response;
import at.uni.innsbruck.htibot.rest.generated.model.HasOpenConversation200Response;
import at.uni.innsbruck.htibot.rest.generated.model.LanguageEnum;
import at.uni.innsbruck.htibot.rest.generated.model.RateConversation200Response;
import at.uni.innsbruck.htibot.rest.util.RestUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.util.ArrayList;
import java.util.Optional;

@ApplicationPath(RestResourceRoot.APPLICATION_PATH)
@ApplicationScoped
public class HTIBotApiImpl extends Application implements HtibotApi {

  private static final String OPERATION_FAILED = "Operation %s failed with %s";

  @Inject
  private ConnectorService connectorService;

  @Inject
  private ConversationService conversationService;

  @Inject
  private KnowledgeService knowledgeService;

  @Inject
  private Logger logger;

  @Inject
  private ConfigProperties configProperties;

  @Inject
  private IncidentReportService incidentReportService;

  @Override
  @NotNull
  public Response continueConversation(@NotNull final String userId) {
    return this.runWithinTryCatch("continueConversation", () -> {
      this.conversationService.continueConversation(userId);
      return Response.ok(new BaseSuccessModel().resultCode(Status.OK.getStatusCode())).build();
    });
  }

  @Override
  @NotNull
  public Response getAnswer(@NotNull final String prompt, final @NotNull String userId,
      final @NotNull LanguageEnum language) {
    return this.runWithinTryCatch("getAnswer", () -> {
      if (this.conversationService.hasOpenConversation(userId)) {
        throw new ConversationNotClosedException(
            "User can not conversate in Conversation that has not been closed or been requested for further conversation.");
      }

      final ConversationLanguage conversationLanguage = RestUtil.fromConversationLanguage(language);
      final Optional<Conversation> conversationOptional = this.conversationService.getOpenConversationByUserId(
          userId);

      Optional<Knowledge> knowledgeOptional = Optional.empty();
      if (conversationOptional.isEmpty() || conversationOptional.orElseThrow().getKnowledge()
          .isEmpty()) {
        knowledgeOptional = this.findKnowledge(prompt, conversationLanguage);
      } else if (conversationOptional.orElseThrow().getKnowledge().isPresent()) {
        knowledgeOptional = conversationOptional.get().getKnowledge();
      }

      final boolean closeConversation = this.isCloseConversation(conversationOptional, knowledgeOptional);
      final String answer = this.connectorService.getAnswer(prompt, knowledgeOptional,
          conversationOptional,
          conversationLanguage, closeConversation);

      Conversation conversation = null;
      if (conversationOptional.isEmpty()) {
        conversation = this.conversationService.createAndSave(
            closeConversation ? Boolean.TRUE : null,
            conversationLanguage, null, userId,
            null,
            new ArrayList<>(),
            knowledgeOptional.map(
                    knowledge -> this.knowledgeService.getById(knowledge.getId()).orElseThrow())
                .orElse(null));
      } else {
        conversation = conversationOptional.orElseThrow();
      }

      this.conversationService.addMessage(conversation, prompt, UserType.USER);
      this.conversationService.addMessage(conversation, answer, UserType.SYSTEM);

      Optional<String> incidentReport = Optional.empty();
      if (closeConversation) {
        this.conversationService.rateConversation(conversation, false);
        this.conversationService.addIncidentReport(conversation,
            this.incidentReportService.createAndSave(answer));
        incidentReport = Optional.of(answer);
      }

      return Response.ok(
          new GetAnswer200Response().answer(answer).resultCode(Status.OK.getStatusCode())
              .autoClosedConversation(closeConversation)
              .incidentReport(incidentReport.orElse(null))).build();
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
    return this.runWithinTryCatch("rateConversation", () -> {
      final Optional<Conversation> conversationOptional = this.conversationService.getOpenConversationByUserId(
          userId);
      Optional<IncidentReport> incidentReport = Optional.empty();
      if (Boolean.FALSE.equals(rating)) {
        incidentReport = Optional.of(
            this.incidentReportService.createAndSave(this.connectorService.generateIncidentReport(
            this.conversationService.getOpenConversationByUserId(userId)
                .orElseThrow(ConversationNotFoundException::new))));
        this.conversationService.addIncidentReport(conversationOptional.orElseThrow(),
            incidentReport.orElseThrow());
      }


      if (conversationOptional.isEmpty()) {
        throw new ConversationNotFoundException(
            String.format("Could not find open conversation for user with id %s", userId));
      }

      this.conversationService.rateConversation(conversationOptional.orElseThrow(), rating);

      return Response.ok(
              new RateConversation200Response().resultCode(Status.OK.getStatusCode())
                  .incidentReport(incidentReport.map(IncidentReport::getText).orElse(null)))
          .build();
    });
  }

  private Response runWithinTryCatch(final String operationId,
      final ExceptionalSupplier<Response> supplier) {
    final long startTime = System.currentTimeMillis();
    try {
      return supplier.get();
    } catch (final PermissionDeniedException e) {
      this.logger.warn(
          String.format(OPERATION_FAILED, operationId, e.getClass().getName()), e);
      return Response.status(Status.UNAUTHORIZED).build();
    } catch (final ConstraintViolationException | IllegalArgumentException | UserIdFinalException |
                   LanguageFinalException e) {
      this.logger.info(
          String.format(OPERATION_FAILED, operationId, e.getClass().getName()), e);
      return Response.status(Status.BAD_REQUEST)
          .entity(new BaseErrorModel().resultCode(Status.BAD_REQUEST.getStatusCode())
              .message(e.getMessage())).build();
    } catch (final ConversationNotClosedException | ConversationClosedException e) {
      this.logger.info(
          String.format(OPERATION_FAILED, operationId, e.getClass().getName()), e);
      return Response.status(Status.CONFLICT)
          .entity(new BaseErrorModel().resultCode(Status.CONFLICT.getStatusCode())
              .message(e.getMessage())).build();
    } catch (final ConversationNotFoundException e) {
      this.logger.info(
          String.format(OPERATION_FAILED, operationId, e.getClass().getName()), e);
      return Response.status(Status.NOT_FOUND)
          .entity(new BaseErrorModel().resultCode(Status.NOT_FOUND.getStatusCode())
              .message(e.getMessage())).build();
    } catch (final Exception e) {
      this.logger.warn(
          String.format(OPERATION_FAILED, operationId, e.getClass().getName()), e);
      return Response.status(Status.INTERNAL_SERVER_ERROR)
          .entity(new BaseErrorModel().resultCode(Status.INTERNAL_SERVER_ERROR.getStatusCode())
              .message(e.getMessage())).build();
    } finally {
      this.logger.info(
          String.format("%s completed in %s ms", operationId,
              System.currentTimeMillis() - startTime));
    }
  }

  private boolean isCloseConversation(final Optional<Conversation> conversationOptional,
      final Optional<Knowledge> knowledgeOptional) {
    return conversationOptional.isPresent() && (
        conversationOptional.orElseThrow().getMessages().size() >
            this.configProperties.getProperty(
                ConfigProperties.HTBOT_MAX_MESSAGES_WITH_KNOWLEDGE) || (
            conversationOptional.orElseThrow().getMessages().size() >
                this.configProperties.getProperty(
                    ConfigProperties.HTBOT_MAX_MESSAGES_WITHOUT_KNOWLEDGE) && (
                knowledgeOptional.isEmpty()
                    && conversationOptional.orElseThrow()
                    .getKnowledge()
                    .isEmpty())));
  }

  private Optional<Knowledge> findKnowledge(final String prompt,
      final ConversationLanguage language) {
    String englishPrompt = null;
    if (!ConversationLanguage.ENGLISH.equals(language)) {
      englishPrompt = this.connectorService.translate(prompt, language,
          ConversationLanguage.ENGLISH);
    } else {
      englishPrompt = prompt;
    }

    return this.knowledgeService.retrieveKnowledge(
        this.connectorService.getEmbedding(englishPrompt));
  }
}
