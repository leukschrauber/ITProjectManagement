package at.uni.innsbruck.htibot.core.util.properties;

import java.util.Locale;

public final class ConfigPropertyBuilder extends PropertyBuilderBase {

  private static final String PROPERTY_PATH = "at.uni.innsbruck.htibot.config";

  private ConfigPropertyBuilder() {
    // static class
  }

  public static String property(final String key, final Object... args) {
    return PropertyBuilderBase.localize(ConfigPropertyBuilder.PROPERTY_PATH, Locale.getDefault(), key, args);
  }

  public static String property(final Locale locale, final String key, final Object... args) {
    return PropertyBuilderBase.localize(ConfigPropertyBuilder.PROPERTY_PATH, locale, key, args);
  }

  public static boolean keyExists(final String key) {
    return PropertyBuilderBase.keyExists(ConfigPropertyBuilder.PROPERTY_PATH, key);
  }

}

