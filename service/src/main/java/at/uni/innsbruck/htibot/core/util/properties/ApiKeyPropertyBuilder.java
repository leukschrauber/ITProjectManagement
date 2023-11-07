package at.uni.innsbruck.htibot.core.util.properties;

import java.util.Locale;

public class ApiKeyPropertyBuilder extends PropertyBuilderBase {

  protected static final String PROPERTY_PATH = "at.uni.innsbruck.htibot.apiKeys";

  protected ApiKeyPropertyBuilder() {
    // static class
  }

  public static String property(final String key) {
    return PropertyBuilderBase.localize(ApiKeyPropertyBuilder.PROPERTY_PATH, Locale.getDefault(), key);
  }

}

