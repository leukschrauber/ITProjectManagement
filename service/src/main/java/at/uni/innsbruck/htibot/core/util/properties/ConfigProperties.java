package at.uni.innsbruck.htibot.core.util.properties;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

@ApplicationScoped
public class ConfigProperties {

  public static final Pair<String, Class<?>> MOCK_OPENAI = new ImmutablePair<>("at.uni.innsbruck.htibot.openai.connector.mockOpenAI",
                                                                               Boolean.class);
  public static final Pair<String, Class<?>> OPENAI_TOKEN = new ImmutablePair<>("at.uni.innsbruck.htibot.openai.connector.token",
                                                                                String.class);
  public static final Pair<String, Class<?>> OPENAI_HOST = new ImmutablePair<>("at.uni.innsbruck.htibot.openai.connector.host",
                                                                               String.class);
  public static final Pair<String, Class<?>> OPENAI_DEPLOYMENT = new ImmutablePair<>(
      "at.uni.innsbruck.htibot.openai.connector.deploymentid", String.class);
  public static final Pair<String, Class<?>> OPENAI_MAX_MESSAGES = new ImmutablePair<>("at.uni.innsbruck.htibot.openai.maxMessagesPerDay",
                                                                                       Integer.class);
  public static final Pair<String, Class<?>> HTBOT_API_KEY = new ImmutablePair<>("at.uni.innsbruck.htibot.apiKey", String.class);
  public static final Pair<String, Class<?>> HTBOT_DATABASE_URL = new ImmutablePair<>("at.uni.innsbruck.htibot.mysql.databaseUrl",
                                                                                      String.class);
  public static final Pair<String, Class<?>> HTBOT_DATABASE_USER = new ImmutablePair<>("at.uni.innsbruck.htibot.mysql.user",
                                                                                       String.class);
  public static final Pair<String, Class<?>> HTBOT_DATABASE_PASSWORD = new ImmutablePair<>("at.uni.innsbruck.htibot.mysql.password",
                                                                                           String.class);
  public static final Pair<String, Class<?>> HTBOT_MAX_MESSAGES_WITHOUT_KNOWLEDGE = new ImmutablePair<>(
      "at.uni.innsbruck.htibot.getAnswer.maxMessagesWithoutKnowledge", Integer.class);
  public static final Pair<String, Class<?>> HTBOT_MAX_MESSAGES_WITH_KNOWLEDGE = new ImmutablePair<>(
      "at.uni.innsbruck.htibot.getAnswer.maxMessagesWithKnowledge", Integer.class);

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
        throw new IllegalArgumentException(String.format("Error loading custom properties file %s", configPath), e);
      }
    } else {
      configPath = DEFAULT_PROPERTY_PATH;
      try (final InputStream input = this.getClass().getClassLoader().getResourceAsStream(configPath)) {
        if (input != null) {
          this.properties.load(input);
        } else {
          throw new IllegalArgumentException("Unable to find specified properties file: " + configPath);
        }
      } catch (final IOException e) {
        throw new IllegalArgumentException("Error loading properties file", e);
      }
    }

    this.validateMandatoryProperties();
    this.validatePropertyTypes();
  }

  private void validateMandatoryProperties() {
    final List<String> mandatoryProperties = Stream.of(OPENAI_HOST, OPENAI_DEPLOYMENT, OPENAI_TOKEN,
                                                       HTBOT_DATABASE_URL, HTBOT_DATABASE_USER, HTBOT_DATABASE_PASSWORD).map(Pair::getLeft)
                                                   .toList();

    for (final String mandatoryProperty : mandatoryProperties) {

      if (Boolean.TRUE.equals(this.getPropertyWithDefault(ConfigProperties.MOCK_OPENAI.getLeft(), Boolean.class, Boolean.FALSE))
          && (mandatoryProperty.toLowerCase().contains("openai"))) {
        continue;
      }
      if (!this.keyExists(mandatoryProperty)) {
        throw new IllegalStateException(
            String.format("Missing properties! The following properties are mandatory: %s", mandatoryProperties));
      }
    }
  }

  private void validatePropertyTypes() {
    final List<Pair<String, Class<?>>> typedProperties = List.of(HTBOT_MAX_MESSAGES_WITH_KNOWLEDGE, HTBOT_MAX_MESSAGES_WITHOUT_KNOWLEDGE,
                                                                 OPENAI_MAX_MESSAGES);

    for (final Pair<String, Class<?>> typedProperty : typedProperties) {
      if (this.keyExists(typedProperty.getLeft())) {
        try {
          if (typedProperty.getRight() == Integer.class) {
            Integer.parseInt(this.getProperty(typedProperty.getLeft(), String.class));
          }
        } catch (final NumberFormatException e) {
          throw new IllegalArgumentException(
              String.format("Property %s is not of type %s, but is expected to be.", typedProperty.getLeft(),
                            typedProperty.getRight().getSimpleName()));
        }
      }
    }
  }


  public boolean keyExists(final String key) {
    return this.properties.containsKey(key);
  }

  public String getProperty(final String key) {
    return this.getProperty(key, String.class);
  }

  public <T> T getProperty(final String key, final Class<T> returnType) {
    if (returnType == Integer.class) {
      return (T) (Integer) Integer.parseInt(this.properties.getProperty(key));
    } else if (returnType == Boolean.class) {
      return (T) (Boolean) Boolean.parseBoolean(this.properties.getProperty(key));
    }
    return (T) this.properties.getProperty(key);
  }

  public <T> T getPropertyWithDefault(final String key, final Class<T> returnType, final T defaultProperty) {
    if (this.keyExists(key)) {
      return this.getProperty(key, returnType);
    }
    return defaultProperty;
  }

}

