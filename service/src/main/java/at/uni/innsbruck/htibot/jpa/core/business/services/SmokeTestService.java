package at.uni.innsbruck.htibot.jpa.core.business.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Transactional(value = Transactional.TxType.REQUIRED, rollbackOn = Throwable.class)
public class SmokeTestService {

/*  public JpaSmokeTest create() {
    final JpaSmokeTest jpaSmokeTest = new JpaSmokeTest();
    jpaSmokeTest.setId(1L);
    return jpaSmokeTest;
  }*/
}