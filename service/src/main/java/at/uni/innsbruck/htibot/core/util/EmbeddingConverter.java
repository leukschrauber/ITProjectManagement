package at.uni.innsbruck.htibot.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class EmbeddingConverter {

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

}
