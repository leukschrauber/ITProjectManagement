package at.uni.innsbruck.htibot.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import at.uni.innsbruck.htibot.core.util.EmbeddingUtil;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class EmbeddingUtilTest {

  @Test
  void verifyGetAsString() {
    final List<Float> vector = List.of(2.2f, -0.2f, 4.9f, 2.22f);
    final String vectorString = EmbeddingUtil.getAsString(vector);
    assertEquals("[2.2,-0.2,4.9,2.22]", vectorString);
  }

  @Test
  void verifyGetAsEmbedding() {
    final String vectorString = "[2.2,-0.2,4.9,2.22]";
    final List<Float> vector = EmbeddingUtil.getAsEmbedding(vectorString);
    assertEquals(4f, vector.size());
    assertEquals(2.2f, vector.get(0));
    assertEquals(-0.2f, vector.get(1));
    assertEquals(4.9f, vector.get(2));
    assertEquals(2.22f, vector.get(3));
  }

  @Test
  void verifyConversion() {
    final List<Float> vector = List.of(2.2f, -0.2f, 4.9f, 2.22f);
    final List<Float> result = EmbeddingUtil.getAsEmbedding(EmbeddingUtil.getAsString(vector));
    assertEquals(4f, result.size());
    assertEquals(2.2f, result.get(0));
    assertEquals(-0.2f, result.get(1));
    assertEquals(4.9f, result.get(2));
    assertEquals(2.22f, result.get(3));
  }

  @Test
  void verifyComputeCosineSimilarity() {
    final List<Float> vector = List.of(2.2f, -0.2f, 4.9f, 2.22f);
    assertEquals(1.0, EmbeddingUtil.computeCosineSimilarity(vector, vector), 0.01);

    final List<Float> oppositeVector = List.of(-2.2f, 0.2f, -4.9f, -2.22f);
    assertEquals(-1.0, EmbeddingUtil.computeCosineSimilarity(vector, oppositeVector), 0.01);
    assertEquals(-1.0, EmbeddingUtil.computeCosineSimilarity(oppositeVector, vector), 0.01);

    final List<Float> ortho1 = List.of(3.0f, 0.0f);
    final List<Float> ortho2 = List.of(0.0f, 4.0f);
    assertEquals(0.0, EmbeddingUtil.computeCosineSimilarity(ortho1, ortho2), 0.01);
    assertEquals(0.0, EmbeddingUtil.computeCosineSimilarity(ortho2, ortho1), 0.01);

    final List<Float> zeroVector = List.of(0.0f);
    assertEquals(0.0, EmbeddingUtil.computeCosineSimilarity(zeroVector, zeroVector), 0.01);

    assertEquals(0.0,
        EmbeddingUtil.computeCosineSimilarity(Collections.emptyList(), Collections.emptyList()), 0.01);
  }

  @Test
  void failComputeCosineSimilarityDifferentVectorSizes() {
    assertThrows(IllegalArgumentException.class,
        () -> EmbeddingUtil.computeCosineSimilarity(List.of(1.0f), List.of()));
  }

}
