package at.uni.innsbruck.htibot.core;

import at.uni.innsbruck.htibot.core.business.services.ConnectorService;
import at.uni.innsbruck.htibot.core.business.services.KnowledgeResourceService;
import at.uni.innsbruck.htibot.core.business.services.KnowledgeService;
import at.uni.innsbruck.htibot.core.business.util.Logger;
import at.uni.innsbruck.htibot.core.exceptions.PersistenceException;
import at.uni.innsbruck.htibot.core.model.enums.UserType;
import at.uni.innsbruck.htibot.core.model.knowledge.Knowledge;
import at.uni.innsbruck.htibot.core.util.properties.ConfigProperties;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Reception;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.flywaydb.core.Flyway;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
  private KnowledgeResourceService knowledgeResourceService;

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
      this.knowledgeService.archiveSystemKnowledge();
      final Path faqPath = Paths.get(
          this.configProperties.getProperty(ConfigProperties.KNOWLEDGE_FAQ_PATH));

      for (final Path faq : Files.list(faqPath)
          .filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".html"))
          .toList()) {
        this.logger.info(
            String.format("Adding knowledge from file %s", faq.getFileName().toString()));
        final Document faqHTMLDocument = Jsoup.parse(new File(faq.toString()), "UTF-8");

        final String questionText = this.connectorService.translateToEnglish(
            this.getQuestionFromHTMLDocument(faqHTMLDocument));
        final String answerText = this.connectorService.translateToEnglish(
            this.getAnswerFromHTMLDocument(faqHTMLDocument));
        final String vectorplaceholder = "fewughiusdfhsdf";
        final Knowledge knowledge = this.knowledgeService.createAndSave(vectorplaceholder,
            questionText,
            answerText,
            UserType.SYSTEM, new HashSet<>(), Boolean.FALSE, faq.getFileName().toString());

        for (final String resourcePath : this.getResourcesFromHTMLDocument(faqPath,
            faqHTMLDocument)) {
          this.knowledgeResourceService.createAndSave(resourcePath, UserType.SYSTEM, knowledge);
        }
      }
      this.logger.info("Done loading FAQs to database.");
    } catch (final Exception e) {
      this.logger.error("Exception while setting up FAQ Knowledge Base.");
      this.logger.error(e);
      throw new IllegalStateException(e);
    }
  }

  @NotNull
  private String getAnswerFromHTMLDocument(final Document faqHTMLDocument) {
    return StringUtils.defaultString(StringUtils.trimToNull(StringUtils.defaultString(
        extractText(faqHTMLDocument, "Problem") + " " + StringUtils.defaultString(
            extractText(faqHTMLDocument, "Solution")))), "No Solution available.");
  }

  @NotNull
  private String getQuestionFromHTMLDocument(final Document faqHTMLDocument) {
    return StringUtils.defaultString(StringUtils.trimToNull(
            StringUtils.defaultString(faqHTMLDocument.selectFirst("title").text()) +
                " " + StringUtils.defaultString(extractText(faqHTMLDocument, "Symptom"))),
        "No Question available.");
  }

  @NotNull
  private static String extractText(final Document document, final String kind)
    {
      final Elements elements = document.select("h2:contains(" + kind + ")");
      if (!elements.isEmpty()) {
        final Element element = elements.first();
        Element nextElement = element.nextElementSibling();
        final StringBuilder textBuilder = new StringBuilder();

        while (nextElement != null && !nextElement.tagName().matches("h\\d")) {
          textBuilder.append(nextElement.text()).append("\n");
          nextElement = nextElement.nextElementSibling();
        }

        return textBuilder.toString().trim();
      }

      return "";
    }

  @NotNull
  private List<String> getResourcesFromHTMLDocument(final Path faqDirectory,
      final Document document) throws PersistenceException {
    return document.select("img").stream()
        .map(imgTag -> faqDirectory.resolve(imgTag.attr("src")).toAbsolutePath().toString())
        .toList();
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
