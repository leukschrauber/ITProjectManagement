package at.uni.innsbruck.htibot.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import at.uni.innsbruck.htibot.core.exceptions.ConversationClosedException;
import at.uni.innsbruck.htibot.core.exceptions.ConversationNotFoundException;
import at.uni.innsbruck.htibot.core.exceptions.LanguageFinalException;
import at.uni.innsbruck.htibot.core.exceptions.PersistenceException;
import at.uni.innsbruck.htibot.core.exceptions.UserIdFinalException;
import at.uni.innsbruck.htibot.core.model.conversation.Conversation;
import at.uni.innsbruck.htibot.core.model.enums.ConversationLanguage;
import at.uni.innsbruck.htibot.core.model.enums.UserType;
import at.uni.innsbruck.htibot.jpa.core.business.services.JpaConversationService;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
class ConversationIT {

  @Inject
  private JpaConversationService conversationService;

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
    assertFalse(this.conversationService.hasOpenConversation(USER_ID));

    this.createConversation();
    assertTrue(this.conversationService.hasOpenConversation(USER_ID));

    Conversation conversation = this.conversationService.continueConversation(USER_ID);
    assertFalse(this.conversationService.hasOpenConversation(USER_ID));

    conversation = this.conversationService.addMessage(conversation, "Need more help",
        UserType.USER);
    assertTrue(this.conversationService.hasOpenConversation(USER_ID));

    this.conversationService.rateConversation(conversation, true);
    assertFalse(this.conversationService.hasOpenConversation(USER_ID));

    conversation = this.createConversation();
    assertTrue(this.conversationService.hasOpenConversation(USER_ID));

    this.conversationService.rateConversation(conversation, false);
    assertFalse(this.conversationService.hasOpenConversation(USER_ID));
  }

  @Test
  void verifyCreateAndSave() throws Exception {
    final Conversation createdConversation = this.createConversation();

    assertEquals(ConversationLanguage.ENGLISH, createdConversation.getLanguage());
    assertTrue(createdConversation.getClosed().isEmpty());
    assertTrue(createdConversation.getRating().isEmpty());
    assertTrue(createdConversation.getKnowledge().isEmpty());
    assertEquals(USER_ID, createdConversation.getUserId());
    assertTrue(createdConversation.getIncidentReport().isEmpty());
    assertEquals(2, createdConversation.getMessages().size());
    assertEquals(UserType.USER, createdConversation.getMessages().get(0).getCreatedBy());
    assertEquals(UserType.SYSTEM, createdConversation.getMessages().get(1).getCreatedBy());
  }

  @Test
  void verifyUpdate() throws Exception {
    final Conversation createdConversation = this.createConversation();

    this.conversationService.update(createdConversation, Boolean.TRUE, ConversationLanguage.ENGLISH,
        Boolean.TRUE, USER_ID, null, new ArrayList<>(), null);

    assertEquals(ConversationLanguage.ENGLISH, createdConversation.getLanguage());
    assertTrue(createdConversation.getClosed().orElseThrow());
    assertTrue(createdConversation.getRating().orElseThrow());
    assertTrue(createdConversation.getKnowledge().isEmpty());
    assertEquals(USER_ID, createdConversation.getUserId());
    assertTrue(createdConversation.getIncidentReport().isEmpty());
    assertEquals(0, createdConversation.getMessages().size());
  }

  @Test
  void failUpdateLanguageChange() throws Exception {
    final Conversation createdConversation = this.createConversation();

    assertThrows(LanguageFinalException.class,
        () -> this.conversationService.update(createdConversation, null,
            ConversationLanguage.GERMAN, null, USER_ID, null, new ArrayList<>(), null));
  }

  @Test
  void failUpdateUserIdChange() throws Exception {
    final Conversation createdConversation = this.createConversation();

    assertThrows(
        UserIdFinalException.class,
        () -> this.conversationService.update(createdConversation, null,
            ConversationLanguage.ENGLISH, null,
            "Ich sach Eintracht, du sachst Trier!", null, new ArrayList<>(), null));
  }

  @Test
  void failUpdateClosedConversation() throws Exception {
    final Conversation positiveConversation = this.createConversation();
    this.conversationService.rateConversation(positiveConversation, true);

    assertThrows(ConversationClosedException.class,
        () -> this.conversationService.update(positiveConversation, null,
            ConversationLanguage.ENGLISH, null, USER_ID, null, new ArrayList<>(), null));

    final Conversation negativeConversation = this.createConversation();
    this.conversationService.rateConversation(negativeConversation, true);

    assertThrows(ConversationClosedException.class,
        () -> this.conversationService.update(negativeConversation, null,
            ConversationLanguage.ENGLISH, null, USER_ID, null, new ArrayList<>(), null));
  }

  @Test
  void verifyAddMessage() throws Exception {
    Conversation createdConversation = this.createConversation();

    createdConversation = this.conversationService.update(createdConversation, Boolean.FALSE,
        ConversationLanguage.ENGLISH, null, USER_ID, null, new ArrayList<>(), null);

    createdConversation = this.conversationService.addMessage(createdConversation, "0",
        UserType.USER);
    createdConversation = this.conversationService.addMessage(createdConversation, "1",
        UserType.SYSTEM);
    createdConversation = this.conversationService.addMessage(createdConversation, "2",
        UserType.USER);
    createdConversation = this.conversationService.addMessage(createdConversation, "3",
        UserType.SYSTEM);

    createdConversation = this.conversationService.reload(createdConversation);

    assertEquals(4, createdConversation.getMessages().size());
    assertEquals("0", createdConversation.getMessages().get(0).getMessage());
    assertEquals("1", createdConversation.getMessages().get(1).getMessage());
    assertEquals("2", createdConversation.getMessages().get(2).getMessage());
    assertEquals("3", createdConversation.getMessages().get(3).getMessage());
  }

  @Test
  void failAddMessageClosedConversation() throws Exception {
    final Conversation positiveConversation = this.createConversation();
    this.conversationService.rateConversation(positiveConversation, true);

    assertThrows(ConversationClosedException.class,
        () -> this.conversationService.addMessage(positiveConversation, "0", UserType.USER));

    final Conversation negativeConversation = this.createConversation();
    this.conversationService.rateConversation(negativeConversation, true);

    assertThrows(ConversationClosedException.class,
        () -> this.conversationService.addMessage(negativeConversation, "0", UserType.USER));
  }

  @Test
  void verifyContinueConversation() throws Exception {

    assertThrows(ConversationNotFoundException.class,
        () -> this.conversationService.continueConversation(USER_ID));

    Conversation conversation = this.createConversation();

    conversation = this.conversationService.continueConversation(USER_ID);

    assertEquals(Boolean.FALSE, conversation.getClosed().orElseThrow());
    assertTrue(conversation.getRating().isEmpty());
  }

  @Test
  void verifyRateConversation() throws Exception {

    final Conversation conversation = this.createConversation();

    final Conversation ratedConversation = this.conversationService.rateConversation(conversation,
        true);

    assertEquals(Boolean.TRUE, conversation.getClosed().orElseThrow());
    assertEquals(Boolean.TRUE, conversation.getRating().orElseThrow());

    assertThrows(ConversationClosedException.class,
        () -> this.conversationService.rateConversation(ratedConversation, false));
  }

  @Test
  void verifyGetOpenConversation() throws Exception {
    assertTrue(this.conversationService.getOpenConversationByUserId(USER_ID).isEmpty());

    final Conversation conversation = this.createConversation();

    assertEquals(conversation.getId(),
        this.conversationService.getOpenConversationByUserId(USER_ID).orElseThrow().getId());
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
