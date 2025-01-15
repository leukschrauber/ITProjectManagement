package at.uni.innsbruck.htibot.dl;

import at.uni.innsbruck.htibot.core.business.services.ConnectorService;
import at.uni.innsbruck.htibot.core.model.conversation.Conversation;
import at.uni.innsbruck.htibot.core.model.knowledge.Knowledge;
import at.uni.innsbruck.htibot.security.ApiKeyRestricted;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

public class MockConnectorService implements ConnectorService {

  @Override
  @NotBlank
  @ApiKeyRestricted
  public String getAnswer(@NotBlank final String prompt,
      final @NotNull Optional<Knowledge> knowledge,
      @NotNull final Optional<Conversation> conversation, final boolean close) {

    final StringBuilder sb = new StringBuilder();
    sb.append("This is an answer from a mocked OpenAI-Service.").append("\n\n");

    sb.append("You have asked me: ").append(prompt).append(".\n\n");

    if (knowledge.isPresent()) {
      sb.append("I will use this answer to reply: ").append(knowledge.orElseThrow().getAnswer())
          .append("\n\n");
    } else {
      sb.append("I have not found any FAQ I could use to answer. ").append("\n\n");
    }

    if (conversation.isPresent()) {
      sb.append(
              String.format("This is part of a conversation in which there were %s messages.",
                  conversation.orElseThrow().getMessages().size()))
          .append("\n");
    } else {
      sb.append("This is the beginning of a conversation.").append("\n\n");
    }


    if (close) {
      sb.append("I have been asked to close this conversation").append("\n\n");
    }

    return sb.toString();
  }

  @NotNull
  @Override
  public List<Float> getEmbedding(final @NotBlank String prompt) {
    return List.of(1.0f, 2.0f, 3.0f);
  }

}
