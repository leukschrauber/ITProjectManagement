package at.uni.innsbruck.htibot.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.apache.commons.lang3.StringUtils;

public final class StringUtil {

  private StringUtil() {
    // static util class
  }


  public static List<String> splitLength(final String value, final int partLength) {

    if (StringUtils.isEmpty(value)) {
      return List.of();
    } else {

      final List<String> tokens = new ArrayList<>((value.length() / partLength) + 1);

      for (int start = 0; start < value.length(); start += partLength) {
        tokens.add(value.substring(start, Math.min(value.length(), start + partLength)));
      }

      return tokens;

    }
  }

  public static String bold(final String value) {
    return String.format("<b>%s</b>", value);
  }

  public static String paragraph(final String value) {
    return String.format("<p>%s</p>", value);
  }

  public static void setNullIfEmpty(final String value, final Consumer<String> setter) {
    setter.accept(StringUtils.isEmpty(value) ? null : value);
  }
}
