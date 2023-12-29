package at.uni.innsbruck.htibot.core.util.properties;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

@ApplicationScoped
public class ConfigProperties {

  public static final ConfigProperty<Boolean> MOCK_OPENAI = new ConfigProperty<>(
      "at.uni.innsbruck.htibot.openai.connector.mockOpenAI",
      Boolean.class, Boolean.FALSE);
  public static final ConfigProperty<String> OPENAI_TOKEN = new ConfigProperty<>(
      "at.uni.innsbruck.htibot.openai.connector.token",
      String.class, null);
  public static final ConfigProperty<String> OPENAI_HOST = new ConfigProperty<>(
      "at.uni.innsbruck.htibot.openai.connector.host",
      String.class, null);
  public static final ConfigProperty<String> OPENAI_GPT_DEPLOYMENT = new ConfigProperty<>(
      "at.uni.innsbruck.htibot.openai.connector.gpt.deploymentid", String.class, null);
  public static final ConfigProperty<String> OPENAI_ADA_DEPLOYMENT = new ConfigProperty<>(
      "at.uni.innsbruck.htibot.openai.connector.ada.deploymentid", String.class, null);
  public static final ConfigProperty<String> HTBOT_API_KEY = new ConfigProperty<>(
      "at.uni.innsbruck.htibot.apiKey", String.class, null);
  public static final ConfigProperty<String> HTBOT_DATABASE_URL = new ConfigProperty<>(
      "at.uni.innsbruck.htibot.mysql.databaseUrl",
      String.class, null);
  public static final ConfigProperty<String> HTBOT_DATABASE_USER = new ConfigProperty<>(
      "at.uni.innsbruck.htibot.mysql.user",
      String.class, "root");
  public static final ConfigProperty<String> HTBOT_DATABASE_PASSWORD = new ConfigProperty<>(
      "at.uni.innsbruck.htibot.mysql.password",
      String.class, "root");
  public static final ConfigProperty<Integer> HTBOT_MAX_MESSAGES_WITHOUT_KNOWLEDGE = new ConfigProperty<>(
      "at.uni.innsbruck.htibot.getAnswer.maxMessagesWithoutKnowledge", Integer.class, 6);
  public static final ConfigProperty<Integer> HTBOT_MAX_MESSAGES_WITH_KNOWLEDGE = new ConfigProperty<>(
      "at.uni.innsbruck.htibot.getAnswer.maxMessagesWithKnowledge", Integer.class, 20);

  public static final ConfigProperty<String> KNOWLEDGE_FAQ_PATH = new ConfigProperty<>(
      "at.uni.innsbruck.htibot.FAQ.path",
      String.class, null);
  public static final ConfigProperty<Boolean> FAQ_CLEAN_UP = new ConfigProperty<>(
      "at.uni.innsbruck.htibot.FAQ.cleanUp",
      Boolean.class, Boolean.FALSE);

  private static final String DEFAULT_PROPERTY_PATH = "at/uni/innsbruck/htibot/config.properties";

  private static final String CONFIGURED_PROPERTY_PATH_VARIABLE = "at.uni.innsbruck.htibot.config.custom";

  private final Properties properties = new Properties();

  @PostConstruct
  public void init() {
    String configPath = System.getProperty(CONFIGURED_PROPERTY_PATH_VARIABLE);

    if (configPath != null) {
      try (final FileInputStream input = new FileInputStream(configPath)) {
        this.properties.load(input);
      } catch (final IOException e) {
        throw new IllegalArgumentException(
            String.format("Error loading custom properties file %s", configPath), e);
      }
    } else {
      configPath = DEFAULT_PROPERTY_PATH;
      try (final InputStream input = this.getClass().getClassLoader()
          .getResourceAsStream(configPath)) {
        if (input != null) {
          this.properties.load(input);
        } else {
          throw new IllegalArgumentException(
              "Unable to find specified properties file: " + configPath);
        }
      } catch (final IOException e) {
        throw new IllegalArgumentException("Error loading properties file", e);
      }
    }

    this.validateMandatoryProperties();
    this.validatePropertyTypes();
  }

  private void validateMandatoryProperties() {
    final List<String> mandatoryProperties = Stream.of(OPENAI_HOST, OPENAI_GPT_DEPLOYMENT,
            OPENAI_TOKEN,
            HTBOT_DATABASE_URL, HTBOT_DATABASE_USER, HTBOT_DATABASE_PASSWORD, KNOWLEDGE_FAQ_PATH,
            OPENAI_ADA_DEPLOYMENT)
        .map(ConfigProperty::getKey)
        .toList();

    for (final String mandatoryProperty : mandatoryProperties) {

      if (Boolean.TRUE.equals(this.getProperty(ConfigProperties.MOCK_OPENAI))
          && (mandatoryProperty.toLowerCase().contains("openai"))) {
        continue;
      }
      if (!this.keyExists(mandatoryProperty)) {
        throw new IllegalStateException(
            String.format("Missing properties! The following properties are mandatory: %s",
                mandatoryProperties));
      }
    }
  }

  private void validatePropertyTypes() {
    final List<ConfigProperty<Integer>> typedProperties = List.of(HTBOT_MAX_MESSAGES_WITH_KNOWLEDGE,
        HTBOT_MAX_MESSAGES_WITHOUT_KNOWLEDGE);

    for (final ConfigProperty<Integer> typedProperty : typedProperties) {
      if (this.keyExists(typedProperty.getKey())) {
        try {
          Integer.parseInt(this.properties.getProperty(typedProperty.getKey()));
        } catch (final NumberFormatException e) {
          throw new IllegalArgumentException(
              String.format("Property %s is not of type %s, but is expected to be.",
                  typedProperty.getKey(),
                  typedProperty.getPropertyClass().getSimpleName()));
        }
      }
    }
  }


  public boolean keyExists(final String key) {
    return this.properties.containsKey(key);
  }

  public <T> T getProperty(final ConfigProperty<T> configProperty) {

    if (!this.keyExists(configProperty.getKey())) {
      return (T) configProperty.getDefaultValue();
    }

    final Class<T> returnType = configProperty.getPropertyClass();

    T returnValue = null;

    if (returnType == Integer.class) {
      returnValue = (T) (Integer) Integer.parseInt(
          this.properties.getProperty(configProperty.getKey()));
    } else if (returnType == Boolean.class) {
      returnValue = (T) (Boolean) Boolean.parseBoolean(
          this.properties.getProperty(configProperty.getKey()));
    } else {
      returnValue = (T) this.properties.getProperty(configProperty.getKey());
    }

    if (returnValue == null) {
      return (T) configProperty.getDefaultValue();
    }

    return returnValue;
  }


}

