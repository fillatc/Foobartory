package org.example;

import static org.example.utils.Constants.ASSEMBLE_FOO_BAR_TIME;
import static org.example.utils.Constants.MINE_BAR_MAX_TIME;
import static org.example.utils.Constants.MINE_BAR_MIN_TIME;
import static org.example.utils.Constants.MINE_FOO_TIME;
import static org.example.utils.Utils.getNextRandom;

import java.util.UUID;
import org.example.domain.ActivityEnum;
import org.example.domain.Bar;
import org.example.domain.Foo;
import org.example.domain.FooBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Robot implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(Robot.class);

  private final UUID serialNumber;
  private final SupplyDepot supplyDepot;
  private ActivityEnum activity;

  public Robot(SupplyDepot supplyDepot, ActivityEnum activity) {
    this.serialNumber = UUID.randomUUID();
    this.supplyDepot = supplyDepot;
    this.activity = activity;
    LOGGER.info("New robot, serialNumber: {}", serialNumber);
  }

  @Override
  public void run() {
    try {
      startWorking();
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void startWorking() throws InterruptedException {
    while (supplyDepot.isRunning()) {
      switch (this.activity) {
        case MINING_FOO -> mineFoo();
        case MINING_BAR -> mineBar();
        case ASSEMBLING_FOO_BAR -> assembleFooBar();
      }

      if (this.supplyDepot.getTotalFoo() >= 5 && this.supplyDepot.getTotalBar() >=5) {
        this.activity = ActivityEnum.ASSEMBLING_FOO_BAR;
      }
    }
  }

  public void mineFoo() throws InterruptedException {
    var foo = new Foo();
    LOGGER.debug("Mining foo: {}.", foo.getSerialNumber());
    Thread.sleep(MINE_FOO_TIME);
    supplyDepot.addFoo(foo);
  }

  public void mineBar() throws InterruptedException {
    var bar = new Bar();
    int result = getNextRandom(MINE_BAR_MIN_TIME, MINE_BAR_MAX_TIME);
    LOGGER.debug("Mining bar: {}", bar.getSerialNumber());
    Thread.sleep(result);
    supplyDepot.addBar(bar);
  }

  public void assembleFooBar() throws InterruptedException {
    var bar = supplyDepot.removeBar();
    var foo = supplyDepot.removeFoo();

    int result = getNextRandom(0, 100);
    Thread.sleep(ASSEMBLE_FOO_BAR_TIME);
    //if (result <= 60) {
      var fooBar = new FooBar();
      LOGGER.debug("Assemble fooBar: {}", fooBar.getSerialNumber());
      supplyDepot.addFooBar(fooBar);
    /*} else {
      LOGGER.debug("Assembling failed! bar: {} | foo: {}", bar.getSerialNumber(), foo.getSerialNumber());
      supplyDepot.addBar(bar);
    }*/
  }


}
