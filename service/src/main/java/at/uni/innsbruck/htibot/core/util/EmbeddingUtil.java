package at.uni.innsbruck.htibot.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class EmbeddingUtil {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @NotNull
  public static String getAsString(@NotNull final List<Double> embedding) {
    try {
      return OBJECT_MAPPER.writeValueAsString(embedding);
    } catch (final JsonProcessingException e) {
      throw new IllegalStateException(
          String.format("Unable to convert embedding %s to String", embedding), e);
    }
  }

  @NotNull
  public static List<Double> getAsEmbedding(@NotNull final String embedding) {
    try {
      return OBJECT_MAPPER.readValue(embedding, new TypeReference<List<Double>>() {
      });
    } catch (final JsonProcessingException e) {
      throw new IllegalStateException(
          String.format("Unable to convert string %s to embedding", embedding), e);
    }
  }

  public static double computeCosineSimilarity(final List<Double> vectorA, final List<Double> vectorB) {
    if (vectorA.size() != vectorB.size()) {
      throw new IllegalArgumentException(
          "Vectors must be of the same length for computing cosine similarity");
    }

    double dotProduct = 0.0;
    double normA = 0.0;
    double normB = 0.0;

    for (int i = 0; i < vectorA.size(); i++) {
      dotProduct += vectorA.get(i) * vectorB.get(i);
      normA += Math.pow(vectorA.get(i), 2);
      normB += Math.pow(vectorB.get(i), 2);
    }

    if (normA == 0.0 || normB == 0.0) {
      return 0.0;
    }

    return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
  }

}
