package at.uni.innsbruck.htibot.core;

import at.uni.innsbruck.htibot.core.business.services.ConnectorService;
import at.uni.innsbruck.htibot.core.business.services.KnowledgeService;
import at.uni.innsbruck.htibot.core.business.util.Logger;
import at.uni.innsbruck.htibot.core.exceptions.KnowledgeNotFoundException;
import at.uni.innsbruck.htibot.core.exceptions.PersistenceException;
import at.uni.innsbruck.htibot.core.model.enums.UserType;
import at.uni.innsbruck.htibot.core.util.EmbeddingUtil;
import at.uni.innsbruck.htibot.core.util.properties.ConfigProperties;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Reception;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import org.flywaydb.core.Flyway;

@Dependent
@WebListener
public class SetupListener implements ServletContextListener {

  @Inject
  private Logger logger;

  @Inject
  private ConfigProperties configProperties;

  @Inject
  private KnowledgeService knowledgeService;

  @Inject
  private ConnectorService connectorService;

  public void onStart(
      @Observes(notifyObserver = Reception.ALWAYS) @Initialized(ApplicationScoped.class) final Object notUsed) {
    this.migrateFlyway();
    this.loadFAQs();
  }

  private void loadFAQs() {

    try {
      this.logger.info("Loading FAQs to database...");
      final Path faqPath = Paths.get(
          this.configProperties.getProperty(ConfigProperties.KNOWLEDGE_FAQ_PATH));
      this.archiveDeletedKnowledge();
      this.addNewFaqs(faqPath);
    } catch (final Exception e) {
      this.logger.error("Exception while setting up FAQ Knowledge Base.");
      this.logger.error(e);
      throw new IllegalStateException(e);
    }
  }

  private void archiveDeletedKnowledge()
      throws PersistenceException {
    if (Boolean.TRUE.equals(this.configProperties.getProperty(ConfigProperties.FAQ_CLEAN_UP))) {
      this.knowledgeService.archiveSystemKnowledge();
    }
  }

  private void addNewFaqs(final Path faqPath) throws IOException, PersistenceException {
    if (Boolean.TRUE.equals(this.configProperties.getProperty(ConfigProperties.FAQ_INIT))) {
      for (final Path faq : Files.list(faqPath)
                                 .filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".csv"))
                                 .toList()) {

        try {
          this.logger.info(
              String.format("Adding knowledge from file %s", faq.getFileName().toString()));
          String delimiter = ";";
          String line;

          try (BufferedReader br = new BufferedReader(Files.newBufferedReader(faq, StandardCharsets.UTF_8))) {
            while ((line = br.readLine()) != null) {
              String[] values = line.split(delimiter);

              if (values.length < 3) {
                this.logger.warn(String.format(
                    "FAQ entry %s in file %s does not hold a question or an answer and is thus not considered.",
                    line, faq.getFileName().toString()));
                continue;
              }

              String question = values[1];
              String answer = values[2];

              final String questionVector = EmbeddingUtil.getAsString(
                  this.connectorService.getEmbedding(question));
              this.knowledgeService.createAndSave(questionVector,
                                                  question,
                                                  answer,
                                                  UserType.SYSTEM, new HashSet<>(), Boolean.FALSE,
                                                  faq.getFileName().toString());
            }
          } catch (IOException e) {
            logger.error("An error occurred while reading the file", e);
          }


        } catch (Exception e) {
          this.logger.warn(String.format("File %s could not be set up",
                                         faq.getFileName().toString()));
        }
      }
      this.logger.info("Done loading FAQs to database.");
    } else {
      this.logger.info("Not initializing FAQs");
    }

  }


  private void migrateFlyway() {
    this.logger.info("Migrating with flyway ...");
    final Flyway flyway = Flyway.configure()
        .dataSource(this.configProperties.getProperty(ConfigProperties.HTBOT_DATABASE_URL),
            this.configProperties.getProperty(ConfigProperties.HTBOT_DATABASE_USER),
            this.configProperties.getProperty(ConfigProperties.HTBOT_DATABASE_PASSWORD))
        .locations("db/migration").load();
    flyway.migrate();
    this.logger.info("Flyway Migration complete.");
  }
}
