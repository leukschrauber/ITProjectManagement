package at.uni.innsbruck.htibot.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import at.uni.innsbruck.htibot.core.business.services.ConversationService;
import at.uni.innsbruck.htibot.core.exceptions.PersistenceException;
import at.uni.innsbruck.htibot.core.model.conversation.Conversation;
import at.uni.innsbruck.htibot.core.model.enums.ConversationLanguage;
import at.uni.innsbruck.htibot.core.model.enums.UserType;
import at.uni.innsbruck.htibot.rest.generated.api.HtibotApi;
import at.uni.innsbruck.htibot.rest.generated.model.HasOpenConversation200Response;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.util.ArrayList;
import java.util.List;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
class HTIBotApiTest {

  @Inject
  private HtibotApi api;

  @Inject
  private ConversationService conversationService;

  private static final List<Conversation> CONVERSATION_CACHE = new ArrayList<>();

  private static final String USER_ID = "Eintracht Trier 05";

  @BeforeEach
  void cleanUp() throws Exception {
    CONVERSATION_CACHE.forEach(conversation -> {
      try {
        this.conversationService.delete(this.conversationService.reload(conversation));
      } catch (final PersistenceException e) {
        throw new RuntimeException(e);
      }
    });
    CONVERSATION_CACHE.clear();
  }

  @Test
  void verifyHasOpenConversation() throws Exception {

    final Response response = this.api.hasOpenConversation(USER_ID);

    assertEquals(Status.OK.getStatusCode(), response.getStatus());
    assertEquals(HasOpenConversation200Response.class, response.getEntity().getClass());
    final HasOpenConversation200Response response200 = (HasOpenConversation200Response) response.getEntity();

    assertFalse(response200.getHasOpenConversation());
    assertEquals(Status.OK.getStatusCode(), response200.getResultCode());
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


}
