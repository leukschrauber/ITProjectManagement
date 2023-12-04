package at.uni.innsbruck.htibot.core.util.properties;

import jakarta.validation.constraints.NotBlank;
import java.util.Optional;

public class ConfigProperty<T> {

  private final String key;

  private Class<T> clazz = null;

  private T defaultValue = null;

  public ConfigProperty(final @NotBlank String key, final Class<T> clazz, final T defaultValue) {
    this.key = key;
    this.clazz = clazz;
    this.defaultValue = defaultValue;
  }

  public String getKey() {
    return this.key;
  }


  public Optional<T> getDefaultValue() {
    return Optional.ofNullable(this.defaultValue);
  }

  public Class<T> getPropertyClass() {
    return this.clazz;
  }
}
