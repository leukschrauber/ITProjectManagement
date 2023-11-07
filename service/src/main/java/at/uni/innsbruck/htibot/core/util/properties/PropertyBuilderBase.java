package at.uni.innsbruck.htibot.core.util.properties;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public abstract class PropertyBuilderBase {

  protected static String localize(final String propertyPath, final Locale locale, final String key, final Object... args) {
    final String baseString = ResourceBundle.getBundle(propertyPath, locale,
                                                       ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_DEFAULT))
                                            .getString(key);
    if (args.length == 0) {
      return baseString;
    }

    return MessageFormat.format(baseString, args);
  }

  protected static boolean keyExists(final String propertyPath, final String key) {
    return ResourceBundle.getBundle(propertyPath,
                                    Locale.getDefault(),
                                    ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_DEFAULT))
                         .containsKey(key);
  }

}

