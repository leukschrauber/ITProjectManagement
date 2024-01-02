package at.uni.innsbruck.htibot.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import at.uni.innsbruck.htibot.core.business.services.ConversationService;
import at.uni.innsbruck.htibot.core.business.services.KnowledgeResourceService;
import at.uni.innsbruck.htibot.core.business.services.KnowledgeService;
import at.uni.innsbruck.htibot.core.exceptions.PersistenceException;
import at.uni.innsbruck.htibot.core.model.conversation.Conversation;
import at.uni.innsbruck.htibot.core.model.enums.ConversationLanguage;
import at.uni.innsbruck.htibot.core.model.enums.UserType;
import at.uni.innsbruck.htibot.core.model.knowledge.Knowledge;
import at.uni.innsbruck.htibot.core.model.knowledge.KnowledgeResource;
import at.uni.innsbruck.htibot.core.util.EmbeddingUtil;
import at.uni.innsbruck.htibot.rest.generated.api.HtibotApi;
import at.uni.innsbruck.htibot.rest.generated.model.BaseErrorModel;
import at.uni.innsbruck.htibot.rest.generated.model.BaseSuccessModel;
import at.uni.innsbruck.htibot.rest.generated.model.GetAnswer200Response;
import at.uni.innsbruck.htibot.rest.generated.model.HasOpenConversation200Response;
import at.uni.innsbruck.htibot.rest.generated.model.LanguageEnum;
import at.uni.innsbruck.htibot.rest.generated.model.RateConversation200Response;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
class HTIBotApiIT {

  @Inject
  private HtibotApi api;

  @Inject
  private ConversationService conversationService;

  @Inject
  private KnowledgeService knowledgeService;

  @Inject
  private KnowledgeResourceService knowledgeResourceService;

  private static final List<Conversation> CONVERSATION_CACHE = new ArrayList<>();

  private static final String USER_ID = "Eintracht Trier 05";

  @AfterEach
  void cleanUp() throws Exception {
    CONVERSATION_CACHE.forEach(conversation -> {
      try {
        this.conversationService.delete(this.conversationService.reload(conversation));
      } catch (final PersistenceException e) {
        throw new RuntimeException(e);
      }
    });
    CONVERSATION_CACHE.clear();
    this.knowledgeService.archiveSystemKnowledge();
  }

  @Test
  void verifyHasOpenConversationNoConversation() throws Exception {
    final Response response = this.api.hasOpenConversation(USER_ID);

    assertEquals(Status.OK.getStatusCode(), response.getStatus());
    assertEquals(HasOpenConversation200Response.class, response.getEntity().getClass());
    final HasOpenConversation200Response response200 = (HasOpenConversation200Response) response.getEntity();

    assertFalse(response200.getHasOpenConversation());
    assertEquals(Status.OK.getStatusCode(), response200.getResultCode());
  }

  @Test
  void verifyHasOpenConversationOpenConversation() throws Exception {
    this.createConversation();

    final Response response = this.api.hasOpenConversation(USER_ID);

    assertEquals(Status.OK.getStatusCode(), response.getStatus());
    assertEquals(HasOpenConversation200Response.class, response.getEntity().getClass());
    final HasOpenConversation200Response response200 = (HasOpenConversation200Response) response.getEntity();

    assertTrue(response200.getHasOpenConversation());
    assertEquals(Status.OK.getStatusCode(), response200.getResultCode());
  }

  @Test
  void verifyContinueConversation() throws Exception {
    this.createConversation();

    final Response response = this.api.continueConversation(USER_ID);

    assertEquals(Status.OK.getStatusCode(), response.getStatus());
    assertEquals(BaseSuccessModel.class, response.getEntity().getClass());
    final BaseSuccessModel response200 = (BaseSuccessModel) response.getEntity();

    assertEquals(Status.OK.getStatusCode(), response200.getResultCode());
  }

  @Test
  void failContinueConversationNoConversation() throws Exception {
    final Response response = this.api.continueConversation(USER_ID);

    assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
    assertEquals(BaseErrorModel.class, response.getEntity().getClass());
    final BaseErrorModel response404 = (BaseErrorModel) response.getEntity();

    assertEquals(Status.NOT_FOUND.getStatusCode(), response404.getResultCode());
    assertEquals(String.format("User %s has no conversation yet.", USER_ID),
        response404.getMessage());
  }

  @Test
  void failGetAnswerConversationNotClosed() throws Exception {
    this.createConversation();

    final Response response = this.api.getAnswer("blabla", USER_ID, LanguageEnum.ENGLISH);
    assertEquals(Status.CONFLICT.getStatusCode(), response.getStatus());
    assertEquals(BaseErrorModel.class, response.getEntity().getClass());
    final BaseErrorModel response409 = (BaseErrorModel) response.getEntity();

    assertEquals(Status.CONFLICT.getStatusCode(), response409.getResultCode());
    assertEquals(
        "User can not conversate in Conversation that has not been closed or been requested for further conversation.",
        response409.getMessage());
  }

  @Test
  void verifyGetAnswerExistingConversation() throws Exception {
    this.createConversation();
    this.conversationService.continueConversation(USER_ID);

    final Response response = this.api.getAnswer("blabla", USER_ID, LanguageEnum.ENGLISH);
    assertEquals(Status.OK.getStatusCode(), response.getStatus());
    assertEquals(GetAnswer200Response.class, response.getEntity().getClass());
    final GetAnswer200Response response200 = (GetAnswer200Response) response.getEntity();

    assertFalse(response200.getAutoClosedConversation());
    assertTrue(StringUtils.isNotBlank(response200.getAnswer()));
    assertEquals(Status.OK.getStatusCode(), response200.getResultCode());
    assertTrue(StringUtils.isEmpty(response200.getIncidentReport()));
  }

  @Test
  void verifyGetAnswerNewConversation() throws Exception {
    final Response response = this.api.getAnswer("blabla", USER_ID, LanguageEnum.ENGLISH);
    assertEquals(Status.OK.getStatusCode(), response.getStatus());
    assertEquals(GetAnswer200Response.class, response.getEntity().getClass());
    final GetAnswer200Response response200 = (GetAnswer200Response) response.getEntity();

    assertFalse(response200.getAutoClosedConversation());
    assertTrue(StringUtils.isNotBlank(response200.getAnswer()));
    assertEquals(Status.OK.getStatusCode(), response200.getResultCode());
    assertTrue(StringUtils.isEmpty(response200.getIncidentReport()));

    this.conversationService.delete(
        this.conversationService.getOpenConversationByUserId(USER_ID).orElseThrow());
  }

  @Test
  void verifyGetAnswerExistingConversationKnowledgeAlreadyAttached() throws Exception {
    final Knowledge knowledge = this.createKnowledge(List.of(1.0, 2.0, 3.0));
    Conversation conversation = this.createConversation();
    conversation = this.conversationService.continueConversation(USER_ID);
    this.conversationService.update(conversation, conversation.getClosed().orElse(null),
        conversation.getLanguage(), conversation.getRating().orElse(null), conversation.getUserId(),
        null, conversation.getMessages(),
        knowledge);

    final Response response = this.api.getAnswer("blabla", USER_ID, LanguageEnum.ENGLISH);
    assertEquals(Status.OK.getStatusCode(), response.getStatus());
    assertEquals(GetAnswer200Response.class, response.getEntity().getClass());
    final GetAnswer200Response response200 = (GetAnswer200Response) response.getEntity();

    assertFalse(response200.getAutoClosedConversation());
    assertTrue(StringUtils.isNotBlank(response200.getAnswer()));
    assertEquals(Status.OK.getStatusCode(), response200.getResultCode());
    assertTrue(StringUtils.isEmpty(response200.getIncidentReport()));
  }

  @Test
  void verifyGetAnswerCloseConversationWithKnowledge() throws Exception {
    this.createKnowledge(List.of(1.0, 2.0, 3.0));
    Conversation conversation = this.createConversation();
    conversation = this.conversationService.continueConversation(USER_ID);
    this.conversationService.update(conversation, conversation.getClosed().orElse(null),
        conversation.getLanguage(), conversation.getRating().orElse(null), conversation.getUserId(),
        null, conversation.getMessages(), null);

    for (int i = 0; i < 4; i++) {
      final Response response = this.api.getAnswer("blabla", USER_ID, LanguageEnum.ENGLISH);
      assertEquals(Status.OK.getStatusCode(), response.getStatus());
      this.api.continueConversation(USER_ID);
    }

    final Response response = this.api.getAnswer("blabla", USER_ID, LanguageEnum.ENGLISH);
    assertEquals(Status.OK.getStatusCode(), response.getStatus());
    assertEquals(GetAnswer200Response.class, response.getEntity().getClass());
    final GetAnswer200Response response200 = (GetAnswer200Response) response.getEntity();

    assertTrue(response200.getAutoClosedConversation());
    assertTrue(StringUtils.isNotBlank(response200.getAnswer()));
    assertEquals(Status.OK.getStatusCode(), response200.getResultCode());
    assertTrue(StringUtils.isNotBlank(response200.getIncidentReport()));
  }

  @Test
  void verifyGetAnswerCloseConversationWithoutKnowledge() throws Exception {
    this.createConversation();
    this.conversationService.continueConversation(USER_ID);

    for (int i = 0; i < 3; i++) {
      this.api.getAnswer("blabla", USER_ID, LanguageEnum.ENGLISH);
      this.api.continueConversation(USER_ID);
    }

    final Response response = this.api.getAnswer("blabla", USER_ID, LanguageEnum.ENGLISH);
    assertEquals(Status.OK.getStatusCode(), response.getStatus());
    assertEquals(GetAnswer200Response.class, response.getEntity().getClass());
    final GetAnswer200Response response200 = (GetAnswer200Response) response.getEntity();

    assertTrue(response200.getAutoClosedConversation());
    assertTrue(StringUtils.isNotBlank(response200.getAnswer()));
    assertEquals(Status.OK.getStatusCode(), response200.getResultCode());
    assertTrue(StringUtils.isNotBlank(response200.getIncidentReport()));
  }

  @Test
  void verifyGetAnswerCloseConversationWithDissimilarKnowledge() throws Exception {
    final Knowledge knowledge = this.createKnowledge(List.of(-0.1, -0.2, -0.3));
    this.createConversation();
    this.conversationService.continueConversation(USER_ID);

    for (int i = 0; i < 3; i++) {
      this.api.getAnswer("blabla", USER_ID, LanguageEnum.ENGLISH);
      this.api.continueConversation(USER_ID);
    }

    final Response response = this.api.getAnswer("blabla", USER_ID, LanguageEnum.ENGLISH);
    assertEquals(Status.OK.getStatusCode(), response.getStatus());
    assertEquals(GetAnswer200Response.class, response.getEntity().getClass());
    final GetAnswer200Response response200 = (GetAnswer200Response) response.getEntity();

    assertTrue(response200.getAutoClosedConversation());
    assertTrue(StringUtils.isNotBlank(response200.getAnswer()));
    assertEquals(Status.OK.getStatusCode(), response200.getResultCode());
    assertTrue(StringUtils.isNotBlank(response200.getIncidentReport()));
  }

  @Test
  void verifyRateConversationPositive() throws Exception {
    this.createConversation();

    final Response response = this.api.rateConversation(USER_ID, true);
    assertEquals(Status.OK.getStatusCode(), response.getStatus());
    assertEquals(RateConversation200Response.class, response.getEntity().getClass());
    final RateConversation200Response response200 = (RateConversation200Response) response.getEntity();

    assertEquals(Status.OK.getStatusCode(), response200.getResultCode());
    assertTrue(
        StringUtils.isEmpty(response200.getIncidentReport()));

  }

  @Test
  void verifyRateConversationNegative() throws Exception {
    this.createConversation();

    final Response response = this.api.rateConversation(USER_ID, false);
    assertEquals(Status.OK.getStatusCode(), response.getStatus());
    assertEquals(RateConversation200Response.class, response.getEntity().getClass());
    final RateConversation200Response response200 = (RateConversation200Response) response.getEntity();

    assertEquals(Status.OK.getStatusCode(), response200.getResultCode());
    assertFalse(
        StringUtils.isBlank(response200.getIncidentReport()));
  }

  @Test
  void failRateConversationNoOpenConversation() throws Exception {
    final Response response = this.api.rateConversation(USER_ID, true);
    assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
    assertEquals(BaseErrorModel.class, response.getEntity().getClass());
    final BaseErrorModel response404 = (BaseErrorModel) response.getEntity();

    assertEquals(Status.NOT_FOUND.getStatusCode(), response404.getResultCode());
    assertEquals(
        String.format("Could not find open conversation for user with id %s", USER_ID),
        response404.getMessage());
  }


  private Conversation createConversation() throws Exception {
    Conversation conversation = this.conversationService.createAndSave(null,
        ConversationLanguage.ENGLISH, null, USER_ID, null, new ArrayList<>(), null);

    this.conversationService.addMessage(conversation, "Help me", UserType.USER);
    conversation = this.conversationService.addMessage(conversation, "Here is your help",
        UserType.SYSTEM);
    CONVERSATION_CACHE.add(conversation);
    return conversation;
  }

  private Knowledge createKnowledge(final List<Double> vector) throws Exception {
    final Knowledge knowledge = this.knowledgeService.createAndSave(
        EmbeddingUtil.getAsString(vector),
        "Whats the question?", "Whats the answer?", UserType.SYSTEM, new HashSet<>(), Boolean.FALSE,
        "find/me/here");
    final KnowledgeResource knowledgeResource = this.knowledgeResourceService.createAndSave(
        "find/me/here", UserType.SYSTEM, knowledge);
    return this.knowledgeService.reload(knowledge);
  }


}
