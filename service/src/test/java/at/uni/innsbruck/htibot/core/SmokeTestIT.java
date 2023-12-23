package at.uni.innsbruck.htibot.core;

import static org.junit.jupiter.api.Assertions.assertFalse;

import at.uni.innsbruck.htibot.jpa.core.business.services.JpaConversationService;
import jakarta.inject.Inject;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
class SmokeTestIT {

  @Inject
  private JpaConversationService conversationService;

  @Test
  void smoketest() throws Exception {
    assertFalse(this.conversationService.hasOpenConversation("1234"));
  }


}
