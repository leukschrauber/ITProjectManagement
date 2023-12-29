package at.uni.innsbruck.htibot.core;

import at.uni.innsbruck.htibot.core.business.util.Logger;
import at.uni.innsbruck.htibot.core.exceptions.PersistenceException;
import at.uni.innsbruck.htibot.core.model.enums.UserType;
import at.uni.innsbruck.htibot.core.model.knowledge.Knowledge;
import at.uni.innsbruck.htibot.core.model.knowledge.KnowledgeResource;
import at.uni.innsbruck.htibot.core.util.properties.ConfigProperties;
import at.uni.innsbruck.htibot.jpa.model.knowledge.JpaKnowledge;
import at.uni.innsbruck.htibot.jpa.model.knowledge.JpaKnowledgeResource;
import at.uni.innsbruck.htibot.jpa.core.business.services.JpaKnowledgeService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Reception;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

import org.flywaydb.core.Flyway;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Dependent
@WebListener
public class SetupListener implements ServletContextListener {

  @Inject
  private Logger logger;

  @Inject
  private ConfigProperties configProperties;

  public void onStart(
      @Observes(notifyObserver = Reception.ALWAYS) @Initialized(ApplicationScoped.class) final Object notUsed)
      throws IOException {
    this.logger.info("Migrating with flyway ...");
    final Flyway flyway = Flyway.configure()
        .dataSource(this.configProperties.getProperty(ConfigProperties.HTBOT_DATABASE_URL),
            this.configProperties.getProperty(ConfigProperties.HTBOT_DATABASE_USER),
            this.configProperties.getProperty(ConfigProperties.HTBOT_DATABASE_PASSWORD))
        .locations("db/migration").load();
    flyway.migrate();
    this.logger.info("Flyway Migration complete.");

    // extracting all html files and ressources from the predefined path
    List<Path> fileList = null;
    String path = this.configProperties.getProperty(ConfigProperties.KNOWLEDGE_FAQ_PATH);
    String resourcepath = this.configProperties.getProperty(ConfigProperties.KNOWLEDGE_RESOURCES_PATH);
    Boolean bool = this.configProperties.getProperty(ConfigProperties.FIRST_START);
    // ist hier ein try catch notwendig?
    try {
      fileList = listFiles(path);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    for (Path filePath : fileList) {
        String filepath = filePath.toString();
        // Rufe funktionen zum Abrufen von Informationen
        String fileName = new File(filepath).getName();
        String number = extractNumberFromFileName(fileName);
        String numberTrimmed = number.substring(number.length() - 3);

        // HTML-Datei laden
        Document document = Jsoup.parse(new File(filepath), "UTF-8");


        // Question vektor herausziehen
        String header = document.selectFirst("title").text(); // das ist der Title des HTML
        String questionText = extractText(document, "Symptom");
        if(questionText.isEmpty())
        {
          questionText = header;
          // Entferne "[User Guide]"
          questionText = questionText.replace("[User Guide]", "").trim();

        }
        String answerText = "Problem: " + extractText(document, "Problem");
        if(answerText.equals("Problem: "))
        {
          answerText = "";
        }
        answerText += "Solution: " + extractText(document, "Solution");

        String vectorplaceholder = "fewughiusdfhsdf";

        Set<KnowledgeResource> resources = generateResourceList(resourcepath, numberTrimmed);

        JpaKnowledge knowledge = new JpaKnowledge(vectorplaceholder,questionText,
            answerText, UserType.SYSTEM);

        knowledge.setKnowledgeResources(resources);

        try {
          JpaKnowledgeService service = new JpaKnowledgeService();

          if(bool)
          {
            service.save(knowledge);
          }
          else {
            service.update(knowledge);
          }
        }
        catch(PersistenceException e)
        {
          System.out.println("Hangt im Error");
          System.out.println(e);
        }



        //test
        System.out.println("Starts here new one:");
        System.out.println(knowledge.getQuestion());
        System.out.println(knowledge.getKnowledgeResources());

      }
      System.out.println("FAQ thing done");
    }
    private static String extractNumberFromFileName(String fileName) {
      // Extrahiere die Nummer aus dem Dateinamen (Annahme: Nummer ist hinter dem ersten "_")
      int indexOfUnderscore = fileName.indexOf("_");

      if (indexOfUnderscore != -1 && indexOfUnderscore + 1 < fileName.length() && fileName.endsWith(".html")) {
        // Extrahiere die Nummer und entferne ".html" am Ende
        String extractedPart = fileName.substring(indexOfUnderscore + 1, fileName.length() - ".html".length());
        return extractedPart;
      }
      return null;
    }

    private static String extractText(Document document, String kind)
    {
      Elements symptomElements = document.select("h2:contains(" + kind + ")");
      if (!symptomElements.isEmpty()) {
        Element symptomElement = symptomElements.first();
        Element nextElement = symptomElement.nextElementSibling();
        StringBuilder textBuilder = new StringBuilder();

        // Iteriere über die nachfolgenden Elemente, bis ein größergeschriebenes Element gefunden wird
        while (nextElement != null && !nextElement.tagName().matches("h\\d")) {
          textBuilder.append(nextElement.text()).append("\n");
          nextElement = nextElement.nextElementSibling();
        }

        return textBuilder.toString().trim();
      }

      return ""; // Rückgabewert, wenn kein Symptom gefunden wurde
    }

    private static List<Path> listFiles(String directoryPath) throws IOException
    {
      return Files.list(Paths.get(directoryPath))
          .filter(Files::isRegularFile)
          .collect(Collectors.toList());
    }
    private static Set<KnowledgeResource> generateResourceList(String baseDirectory, String number) {
      Set<KnowledgeResource> matchingFilePaths = null;
      File directory = new File(baseDirectory);
      File[] files = directory.listFiles();

      if (files != null) {
        for (File file : files) {
          String fileName = file.getName();
          fileName = fileName.substring(0, Math.min(fileName.length(), 3));
          if (fileName.equals(number)) {
            JpaKnowledgeResource filepathresource = new JpaKnowledgeResource(file.getAbsolutePath().toString(), UserType.SYSTEM);
            matchingFilePaths.add(filepathresource);
          }
        }
        return matchingFilePaths;
      }
      return null;
    }
}
