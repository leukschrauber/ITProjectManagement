package at.uni.innsbruck.htibot.core;

import at.uni.innsbruck.htibot.core.business.util.Logger;
import at.uni.innsbruck.htibot.core.util.properties.ConfigProperties;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Reception;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.flywaydb.core.Flyway;

@Dependent
@WebListener
public class SetupListener implements ServletContextListener {

  @Inject
  private Logger logger;

  @Inject
  private ConfigProperties configProperties;

  public void onStart(@Observes(notifyObserver = Reception.ALWAYS) @Initialized(ApplicationScoped.class) final Object notUsed) {
    this.logger.info("Migrating with flyway ...");
    final Flyway flyway = Flyway.configure()
                                .dataSource(this.configProperties.getProperty(ConfigProperties.HTBOT_DATABASE_URL.getLeft()),
                                            this.configProperties.getProperty(ConfigProperties.HTBOT_DATABASE_USER.getLeft()),
                                            this.configProperties.getProperty(ConfigProperties.HTBOT_DATABASE_PASSWORD.getLeft()))
                                .locations("db/migration").load();
    flyway.migrate();
    this.logger.info("Flyway Migration complete.");
  }


}
