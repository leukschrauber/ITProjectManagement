package at.uni.innsbruck.htibot.core;

import at.uni.innsbruck.htibot.core.business.util.Logger;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Reception;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@Dependent
@WebListener
public class SetupListener implements ServletContextListener {


  @Inject
  private Logger logger;

  public void onStart(@Observes(notifyObserver = Reception.ALWAYS) @Initialized(ApplicationScoped.class) final Object pointless) {
    this.logger.info("Logging");

  }


}
