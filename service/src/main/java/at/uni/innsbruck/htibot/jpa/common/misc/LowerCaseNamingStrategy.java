package at.uni.innsbruck.htibot.jpa.common.misc;

import org.hibernate.boot.model.naming.Identifier;

public class LowerCaseNamingStrategy extends AbstractBaseNamingStrategy {

  @Override
  protected Identifier handle(final Identifier identifier) {

    final String regex = "([a-z])([A-Z])";
    final String replacement = "$1_$2";
    final String newName = identifier.getText()
                                     .replaceAll(regex, replacement)
                                     .toLowerCase();
    return Identifier.toIdentifier(newName);

  }

}
