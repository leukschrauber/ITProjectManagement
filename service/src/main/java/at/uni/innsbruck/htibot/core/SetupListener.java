package at.uni.innsbruck.htibot.core;

import at.uni.innsbruck.htibot.core.business.services.ConnectorService;
import at.uni.innsbruck.htibot.core.business.services.KnowledgeResourceService;
import at.uni.innsbruck.htibot.core.business.services.KnowledgeService;
import at.uni.innsbruck.htibot.core.business.util.Logger;
import at.uni.innsbruck.htibot.core.exceptions.KnowledgeNotFoundException;
import at.uni.innsbruck.htibot.core.exceptions.PersistenceException;
import at.uni.innsbruck.htibot.core.model.enums.UserType;
import at.uni.innsbruck.htibot.core.model.knowledge.Knowledge;
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
import java.io.File;
import java.io.IOException;
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
      final Path faqPath = Paths.get(
          this.configProperties.getProperty(ConfigProperties.KNOWLEDGE_FAQ_PATH));
      this.archiveDeletedKnowledge(faqPath);
      this.addNewFaqs(faqPath);
    } catch (final Exception e) {
      this.logger.error("Exception while setting up FAQ Knowledge Base.");
      this.logger.error(e);
      throw new IllegalStateException(e);
    }
  }

  private void archiveDeletedKnowledge(final Path faqPath)
      throws IOException, KnowledgeNotFoundException, PersistenceException {
    if (Boolean.TRUE.equals(this.configProperties.getProperty(ConfigProperties.FAQ_CLEAN_UP))) {
      this.knowledgeService.archiveSystemKnowledge();
    } else {
      final List<String> existingFileNames = this.knowledgeService.getKnowledgeFileNames();
      final List<String> newFileNames = Files.list(faqPath)
          .filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".html"))
          .map(file -> file.getFileName().toString())
          .toList();

      for (final String existingFileName : existingFileNames) {
        if (!newFileNames.contains(existingFileName)) {
          this.logger.info(String.format(
              "Removing %s from knowledge base, as it is not included in the FAQ data anymore.",
              existingFileName));
          this.knowledgeService.archiveSystemKnowledge(existingFileName);
        }
      }
    }
  }

  private void addNewFaqs(final Path faqPath) throws IOException, PersistenceException {
    final List<String> existingfileNames = this.knowledgeService.getKnowledgeFileNames();

    for (final Path faq : Files.list(faqPath)
        .filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".html"))
        .toList()) {

      if (existingfileNames.contains(faq.getFileName().toString())) {
        this.logger.info(String.format("File %s already exists in knowledge base.",
            faq.getFileName().toString()));
        continue;
      }

      this.logger.info(
          String.format("Adding knowledge from file %s", faq.getFileName().toString()));
      final Document faqHTMLDocument = Jsoup.parse(new File(faq.toString()), "UTF-8");

      final String questionText =
          this.getQuestionFromHTMLDocument(faqHTMLDocument);
      final String answerText =
          this.getAnswerFromHTMLDocument(faqHTMLDocument);

      if (StringUtils.isBlank(questionText) || StringUtils.isBlank(answerText)) {
        this.logger.warn(String.format(
            "FAQ file %s does not hold a question or an answer and is thus not considered.",
            faq.getFileName().toString()));
        continue;
      }

      final String questionVector = EmbeddingUtil.getAsString(
          this.connectorService.getEmbedding(questionText));
      final Knowledge knowledge = this.knowledgeService.createAndSave(questionVector,
          this.connectorService.translateToEnglish(questionText),
          this.connectorService.translateToEnglish(answerText),
          UserType.SYSTEM, new HashSet<>(), Boolean.FALSE, faq.getFileName().toString());

      for (final String resourcePath : this.getResourcesFromHTMLDocument(faqPath,
          faqHTMLDocument)) {

        if (new File(resourcePath).exists() || StringUtils.startsWith(resourcePath, "http")) {
          this.knowledgeResourceService.createAndSave(resourcePath, UserType.SYSTEM, knowledge);
        } else {
          this.logger.warn(
              String.format("Resource %s in FAQ %s does not exist.",
                  knowledge.getFilename().orElseThrow(),
                  resourcePath));
        }

      }
    }
    this.logger.info("Done loading FAQs to database.");
  }

  @NotNull
  private String getAnswerFromHTMLDocument(final Document faqHTMLDocument) {
    return StringUtils.trimToNull(StringUtils.defaultString(
        extractText(faqHTMLDocument, "Problem") + " " + StringUtils.defaultString(
            extractText(faqHTMLDocument, "Solution"))));
  }

  @NotNull
  private String getQuestionFromHTMLDocument(final Document faqHTMLDocument) {
    return StringUtils.trimToNull(
            StringUtils.defaultString(faqHTMLDocument.selectFirst("title").text()) +
                " " + StringUtils.defaultString(extractText(faqHTMLDocument, "Symptom")));
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
      final Document document) {
    return document.select("img").stream()
        .map(imgTag -> {
          if (StringUtils.contains(imgTag.attr("src"), "http")) {
            return imgTag.attr("src");
          } else {
            return faqDirectory.resolve(imgTag.attr("src")).toAbsolutePath().toString();
          }
        })
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
