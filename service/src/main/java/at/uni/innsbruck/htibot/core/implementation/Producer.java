package at.uni.innsbruck.htibot.core.implementation;

import at.uni.innsbruck.htibot.core.business.util.Logger;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;

@ApplicationScoped
public class Producer {

  @Produces
  @Dependent
  public Logger createLogger(final InjectionPoint ip) {
    return new SLF4JLogger(ip.getMember().getDeclaringClass());
  }

}
