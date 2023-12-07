package at.uni.innsbruck.htibot.dl;

import at.uni.innsbruck.htibot.core.business.services.ConnectorService;
import at.uni.innsbruck.htibot.core.util.properties.ConfigProperties;
import at.uni.innsbruck.htibot.dl.botinstructions.BotInstructionResolver;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

@ApplicationScoped
public class OpenAIConnectorServiceProducer {


  @Inject
  private ConfigProperties configProperties;

  @Inject
  private BotInstructionResolver botInstructionResolver;

  @Produces
  @ApplicationScoped
  public ConnectorService produceMyService() {
    if (this.configProperties.getProperty(ConfigProperties.MOCK_OPENAI)) {
      return new MockConnectorService((this.configProperties));
    }
    return new OpenAIConnectorService(this.configProperties, this.botInstructionResolver);
  }
}
