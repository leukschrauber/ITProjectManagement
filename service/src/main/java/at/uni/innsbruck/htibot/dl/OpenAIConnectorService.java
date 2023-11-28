package at.uni.innsbruck.htibot.dl;

import at.uni.innsbruck.htibot.core.business.services.ConnectorService;
import at.uni.innsbruck.htibot.core.model.knowledge.Knowledge;
import at.uni.innsbruck.htibot.core.util.properties.ConfigPropertyBuilder;
import at.uni.innsbruck.htibot.rest.generated.model.LanguageEnum;
import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.Completions;
import com.azure.ai.openai.models.CompletionsOptions;
import com.azure.core.credential.AzureKeyCredential;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class OpenAIConnectorService implements ConnectorService {

  private OpenAIClient openAIClient;

  private String deploymentId;

  @PostConstruct
  public void init() {
    final String azureOpenaiKey = ConfigPropertyBuilder.property("at.uni.innsbruck.htibot.openai.connector.token");
    final String endpoint = ConfigPropertyBuilder.property("at.uni.innsbruck.htibot.openai.connector.host");
    this.deploymentId = ConfigPropertyBuilder.property("at.uni.innsbruck.htibot.openai.connector.deploymentid");

    this.openAIClient = new OpenAIClientBuilder()
        .endpoint(endpoint)
        .credential(new AzureKeyCredential(azureOpenaiKey))
        .buildClient();
  }

  @Override
  @NotBlank
  public String getAnswer(@NotBlank final String prompt, final @NotNull Optional<Knowledge> knowledge,
                          @NotNull final LanguageEnum language) {
    final List<String> promptList = Collections.singletonList(prompt);

    final Completions completions = this.openAIClient.getCompletions(this.deploymentId,
                                                                     new CompletionsOptions(promptList).setMaxTokens(200));

    return completions.getChoices().stream().findFirst().orElseThrow().getText();
  }

  @Override
  @NotBlank
  public String translate(@NotBlank final String prompt, @NotNull final LanguageEnum from, @NotNull final LanguageEnum to) {
    return null;
  }
}
