package at.uni.innsbruck.htibot.core;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jboss.arquillian.junit5.ArquillianExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
public class SmokeTestIT  {

  @Test
  public void smoketest() throws Exception {
    assertTrue(true);
  }


}
