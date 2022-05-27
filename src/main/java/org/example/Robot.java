package org.example;

import static org.example.utils.Constants.ASSEMBLE_FOO_BAR_TIME;
import static org.example.utils.Constants.BUY_ROBOT_TIME;
import static org.example.utils.Constants.MINE_BAR_MAX_TIME;
import static org.example.utils.Constants.MINE_BAR_MIN_TIME;
import static org.example.utils.Constants.MINE_FOO_TIME;
import static org.example.utils.Constants.SELL_FOO_BAR_TIME;
import static org.example.utils.Utils.getNextRandom;


import java.util.List;
import java.util.UUID;
import org.example.domain.ActivityEnum;
import org.example.domain.Bar;
import org.example.domain.Foo;
import org.example.domain.FooBar;
import org.example.utils.Utils;
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
    LOGGER.debug("Start working!");
    startWorking();
  }

  private void startWorking() {
    while (supplyDepot.isRunning()) {
      ActivityEnum newActivity;
      if (shouldSwitch()) {
        LOGGER.debug("Force switch!");
        newActivity = supplyDepot.getTotalFoo() > supplyDepot.getTotalBar() ? ActivityEnum.MINING_BAR : ActivityEnum.MINING_FOO;
      } else {
        newActivity = searchActivity();
      }
      boolean switchingActivity = this.activity != newActivity;
      this.activity = newActivity;

      switch (this.activity) {
        case MINING_FOO -> mineFoo(switchingActivity);
        case MINING_BAR -> mineBar(switchingActivity);
        case ASSEMBLING_FOO_BAR -> assembleFooBar(switchingActivity);
        case SELLING_FOO_BAR -> sellFooBar(switchingActivity);
        case BUYING_ROBOT -> buyRobot(switchingActivity);
        default -> throw new RuntimeException(String.format("Unknown activity %s", this.activity));
      }
    }
  }

  private boolean shouldSwitch() {
    return switch (activity) {
      case ASSEMBLING_FOO_BAR -> supplyDepot.getTotalFoo() == 0 || supplyDepot.getTotalBar() == 0;
      case BUYING_ROBOT -> supplyDepot.getTotalMoney() < 3 || supplyDepot.getTotalFoo() < 6;
      case SELLING_FOO_BAR -> supplyDepot.getTotalFooBar() < 5;
      default -> false;
    };
  }

  private ActivityEnum searchActivity() {
    if (supplyDepot.getTotalMoney() >= 3 && supplyDepot.getTotalFoo() >= 6) {
      return ActivityEnum.BUYING_ROBOT;
    } else if (supplyDepot.getTotalFooBar() >= 5) {
      return ActivityEnum.SELLING_FOO_BAR;
    } else if (supplyDepot.getTotalFoo() >= 11 || supplyDepot.getTotalBar() >= 5) {
      return ActivityEnum.ASSEMBLING_FOO_BAR;
    }
    return this.activity;
  }
  public void mineFoo(boolean switchingActivity) {
    var foo = new Foo();
    LOGGER.debug("Mining foo: {}.", foo.getSerialNumber());
    Utils.sleep(MINE_FOO_TIME, switchingActivity);
    supplyDepot.addFoo(foo);
  }

  public void mineBar(boolean switchingActivity) {
    var bar = new Bar();
    int result = getNextRandom(MINE_BAR_MIN_TIME, MINE_BAR_MAX_TIME);
    LOGGER.debug("Mining bar: {}", bar.getSerialNumber());
    Utils.sleep(result, switchingActivity);
    supplyDepot.addBar(bar);
  }

  private void assembleFooBar(boolean switchingActivity) {
    var bar = supplyDepot.removeBar();
    var foo = supplyDepot.removeFoo();

    if (foo == null || bar == null) {
      if (foo != null) {
        LOGGER.warn("Can't assemble foobar  foo...");
        supplyDepot.addFoo(foo);
      } else {
        LOGGER.warn("Can't assemble foobar  bar...");
        supplyDepot.addBar(bar);
      }
    } else {
      int result = getNextRandom(0, 100);
      Utils.sleep(ASSEMBLE_FOO_BAR_TIME, switchingActivity);
      if (result <= 60) {
        var fooBar = new FooBar();
        LOGGER.debug("Assemble fooBar: {}", fooBar.getSerialNumber());
        supplyDepot.addFooBar(fooBar);
      } else {
        LOGGER.debug("Assembling failed! bar: {} | foo: {}", bar.getSerialNumber(), foo.getSerialNumber());
        supplyDepot.addBar(bar);
      }
    }
  }

  private void sellFooBar(boolean switchingActivity) {
    List<FooBar> fooBars = supplyDepot.removeFiveFooBar();
    if (fooBars.size() != 5) {
      LOGGER.warn("Can't sell foobar...");
      fooBars.forEach(supplyDepot::addFooBar);
    }
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Sell fooBar: {}.", String.join("," + fooBars.stream().map(FooBar::getSerialNumber).toList()));
    }
    Utils.sleep(SELL_FOO_BAR_TIME, switchingActivity);
    supplyDepot.addMoney(5);
  }

  private void buyRobot(boolean switchingActivity) {
    List<Foo> fooList = supplyDepot.removeFoo(6);
    var balance = supplyDepot.removeMoney(1);
    if (fooList.size() != 6 || balance < 0) {
      if (fooList.size() != 6) {
        LOGGER.warn("Can't buy robot...");
        fooList.forEach(supplyDepot::addFoo);
      } else {
        LOGGER.warn("Can't buy robot money...");
        supplyDepot.addMoney(1);
      }
    } else {
      LOGGER.info("Buying a new robot.");
      Utils.sleep(BUY_ROBOT_TIME, switchingActivity);
      supplyDepot.addRobot(ActivityEnum.MINING_BAR);
    }
  }

}
