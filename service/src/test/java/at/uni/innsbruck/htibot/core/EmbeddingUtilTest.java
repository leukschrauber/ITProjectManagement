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
    final List<Double> vector = List.of(2.2, -0.2, 4.9, 2.22);
    final String vectorString = EmbeddingUtil.getAsString(vector);
    assertEquals("[2.2,-0.2,4.9,2.22]", vectorString);
  }

  @Test
  void verifyGetAsEmbedding() {
    final String vectorString = "[2.2,-0.2,4.9,2.22]";
    final List<Double> vector = EmbeddingUtil.getAsEmbedding(vectorString);
    assertEquals(4, vector.size());
    assertEquals(2.2, vector.get(0));
    assertEquals(-0.2, vector.get(1));
    assertEquals(4.9, vector.get(2));
    assertEquals(2.22, vector.get(3));
  }

  @Test
  void verifyConversion() {
    final List<Double> vector = List.of(2.2, -0.2, 4.9, 2.22);
    final List<Double> result = EmbeddingUtil.getAsEmbedding(EmbeddingUtil.getAsString(vector));
    assertEquals(4, result.size());
    assertEquals(2.2, result.get(0));
    assertEquals(-0.2, result.get(1));
    assertEquals(4.9, result.get(2));
    assertEquals(2.22, result.get(3));
  }

  @Test
  void verifyComputeCosineSimilarity() {
    final List<Double> vector = List.of(2.2, -0.2, 4.9, 2.22);
    assertEquals(1.0, EmbeddingUtil.computeCosineSimilarity(vector, vector));

    final List<Double> oppositeVector = List.of(-2.2, 0.2, -4.9, -2.22);
    assertEquals(-1.0, EmbeddingUtil.computeCosineSimilarity(vector, oppositeVector));
    assertEquals(-1.0, EmbeddingUtil.computeCosineSimilarity(oppositeVector, vector));

    final List<Double> ortho1 = List.of(3.0, 0.0);
    final List<Double> ortho2 = List.of(0.0, 4.0);
    assertEquals(0.0, EmbeddingUtil.computeCosineSimilarity(ortho1, ortho2));
    assertEquals(0.0, EmbeddingUtil.computeCosineSimilarity(ortho2, ortho1));

    final List<Double> zeroVector = List.of(0.0);
    assertEquals(0.0, EmbeddingUtil.computeCosineSimilarity(zeroVector, zeroVector));

    assertEquals(0.0,
        EmbeddingUtil.computeCosineSimilarity(Collections.emptyList(), Collections.emptyList()));
  }

  @Test
  void failComputeCosineSimilarityDifferentVectorSizes() {
    assertThrows(IllegalArgumentException.class,
        () -> EmbeddingUtil.computeCosineSimilarity(List.of(1.0), List.of()));
  }

}
