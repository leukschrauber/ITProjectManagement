package at.uni.innsbruck.htibot.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import at.uni.innsbruck.htibot.core.business.services.KnowledgeResourceService;
import at.uni.innsbruck.htibot.core.business.services.KnowledgeService;
import at.uni.innsbruck.htibot.core.exceptions.KnowledgeNotFoundException;
import at.uni.innsbruck.htibot.core.model.enums.UserType;
import at.uni.innsbruck.htibot.core.model.knowledge.Knowledge;
import at.uni.innsbruck.htibot.core.model.knowledge.KnowledgeResource;
import at.uni.innsbruck.htibot.core.util.EmbeddingUtil;
import jakarta.inject.Inject;
import java.util.HashSet;
import java.util.List;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
class KnowledgeIT {

  @Inject
  private KnowledgeService knowledgeService;

  @Inject
  private KnowledgeResourceService knowledgeResourceService;

  @AfterEach
  void cleanUp() throws Exception {
    this.knowledgeService.archiveSystemKnowledge();
  }

  @Test
  void verifyCreateAndSave() throws Exception {
    final List<Double> knowledgeVector = List.of(1.0, -2.0, 3.0);
    Knowledge knowledge = this.createKnowledge(knowledgeVector);
    knowledge = this.knowledgeService.reload(knowledge);

    assertEquals(1, knowledge.getKnowledgeResources().size());
    assertEquals("find/me/here",
        knowledge.getKnowledgeResources().stream().findFirst().orElseThrow().getResourcePath());
    assertEquals(UserType.SYSTEM,
        knowledge.getKnowledgeResources().stream().findFirst().orElseThrow().getCreatedBy());
    assertEquals(knowledge,
        knowledge.getKnowledgeResources().stream().findFirst().orElseThrow().getKnowledge());

    assertEquals(knowledgeVector.size(), knowledge.getQuestionVector().size());
    assertEquals(1.0, knowledge.getQuestionVector().get(0));
    assertEquals(-2.0, knowledge.getQuestionVector().get(1));
    assertEquals(3.0, knowledge.getQuestionVector().get(2));
    assertEquals(EmbeddingUtil.getAsString(knowledgeVector), knowledge.getQuestionVectorString());
    assertEquals("Whats the question?", knowledge.getQuestion());
    assertEquals("Whats the answer?", knowledge.getAnswer());
    assertEquals("find/me/here", knowledge.getFilename().orElseThrow());
    assertEquals(UserType.SYSTEM, knowledge.getCreatedBy());
    assertEquals(Boolean.FALSE, knowledge.getArchived());
  }

  @Test
  void verifyRetrieveKnowledge() throws Exception {
    final List<Double> similarityVector = List.of(2.0, 3.9, 4.0);
    final List<Double> dissimilarityVector = List.of(-0.5, -0.2, -0.1);

    final Knowledge similarKnowledge = this.createKnowledge(similarityVector);
    this.createKnowledge(dissimilarityVector);
    final Knowledge retrieved = this.knowledgeService.retrieveKnowledge(similarityVector)
        .orElseThrow();
    assertEquals(similarKnowledge, retrieved);

    this.knowledgeService.archiveSystemKnowledge();
    this.createKnowledge(dissimilarityVector);
    assertTrue(this.knowledgeService.retrieveKnowledge(similarityVector).isEmpty());
  }

  @Test
  void verifyArchiveSystemKnowledgeByFilename() throws Exception {
    final List<Double> knowledgeVector = List.of(1.0, 2.0, 3.0);
    Knowledge knowledge = this.createKnowledge(knowledgeVector);

    assertEquals(Boolean.FALSE, knowledge.getArchived());
    this.knowledgeService.archiveSystemKnowledge("find/me/here");

    knowledge = this.knowledgeService.reload(knowledge);
    assertEquals(Boolean.TRUE, knowledge.getArchived());
  }

  @Test
  void verifyArchiveSystemKnowledge() throws Exception {
    final List<Double> knowledgeVector = List.of(1.0, 2.0, 3.0);
    Knowledge knowledge = this.createKnowledge(knowledgeVector);
    Knowledge knowledge2 = this.createKnowledge(knowledgeVector);

    assertEquals(Boolean.FALSE, knowledge.getArchived());
    assertEquals(Boolean.FALSE, knowledge2.getArchived());

    this.knowledgeService.archiveSystemKnowledge();

    knowledge = this.knowledgeService.reload(knowledge);
    knowledge2 = this.knowledgeService.reload(knowledge2);

    assertEquals(Boolean.TRUE, knowledge.getArchived());
    assertEquals(Boolean.TRUE, knowledge2.getArchived());
  }

  @Test
  void failArchiveSystemKnowledgeByFilename() throws Exception {
    assertThrows(KnowledgeNotFoundException.class,
        () -> this.knowledgeService.archiveSystemKnowledge("find/me/here"));

    final List<Double> knowledgeVector = List.of(1.0, 2.0, 3.0);
    this.createKnowledge(knowledgeVector);
    this.knowledgeService.archiveSystemKnowledge("find/me/here");

    assertThrows(KnowledgeNotFoundException.class,
        () -> this.knowledgeService.archiveSystemKnowledge("find/me/here"));
  }

  @Test
  void verifyGetKnowledgeFileNames() throws Exception {
    final List<Double> knowledgeVector = List.of(1.0, 2.0, 3.0);
    this.createKnowledge(knowledgeVector);

    final List<String> knowledgeFileNames = this.knowledgeService.getKnowledgeFileNames();
    final List<String> expectedList = List.of("find/me/here");

    assertEquals(1, knowledgeFileNames.size());
    assertTrue(knowledgeFileNames.containsAll(expectedList));
    assertTrue(expectedList.containsAll(knowledgeFileNames));

    this.knowledgeService.archiveSystemKnowledge();

    assertEquals(0, this.knowledgeService.getKnowledgeFileNames().size());
  }

  private Knowledge createKnowledge(final List<Double> vector) throws Exception {
    final Knowledge knowledge = this.knowledgeService.createAndSave(
        EmbeddingUtil.getAsString(vector),
        "Whats the question?", "Whats the answer?", UserType.SYSTEM, new HashSet<>(), Boolean.FALSE,
        "find/me/here");
    final KnowledgeResource knowledgeResource = this.knowledgeResourceService.createAndSave(
        "find/me/here", UserType.SYSTEM, knowledge);
    return this.knowledgeService.reload(knowledge);
  }

}
