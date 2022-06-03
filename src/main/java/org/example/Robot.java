package org.example;

import static org.example.utils.Constants.ASSEMBLE_FOO_BAR_TIME;
import static org.example.utils.Constants.BUY_ROBOT_TIME;
import static org.example.utils.Constants.MINE_BAR_MAX_TIME;
import static org.example.utils.Constants.MINE_BAR_MIN_TIME;
import static org.example.utils.Constants.MINE_FOO_TIME;
import static org.example.utils.Constants.SELL_FOO_BAR_TIME;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.example.domain.ActivityEnum;
import org.example.domain.Bar;
import org.example.domain.Foo;
import org.example.domain.FooBar;
import org.example.utils.RandomSingleton;
import org.example.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class Robot implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(Robot.class);

  private final UUID serialNumber;
  private final SupplyDepot supplyDepot;
  private ActivityEnum activity;
  private RandomSingleton random;

  public Robot(SupplyDepot supplyDepot, RandomSingleton random, ActivityEnum activity) {
    this.serialNumber = UUID.randomUUID();
    this.supplyDepot = supplyDepot;
    this.random = random;
    this.activity = activity;
    LOGGER.info("New robot, serialNumber: {}", serialNumber);
  }

  @Override
  public void run() {
    LOGGER.debug("Start working!");
    startWorking();
  }

  private void startWorking() {
    MDC.put("robot", this.serialNumber.toString());
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
    MDC.clear();
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
    int result = random.getNextRandom(MINE_BAR_MIN_TIME, MINE_BAR_MAX_TIME);
    LOGGER.debug("Mining bar: {}", bar.getSerialNumber());
    Utils.sleep(result, switchingActivity);
    supplyDepot.addBar(bar);
  }

  public void assembleFooBar(boolean switchingActivity) {
    var bar = supplyDepot.removeBar();
    var foo = supplyDepot.removeFoo();

    if (foo == null || bar == null) {
      if (foo != null) {
        LOGGER.warn("Can't assemble foobar foo is null!");
        supplyDepot.addFoo(foo);
      } else {
        LOGGER.warn("Can't assemble foobar bar is null!");
        supplyDepot.addBar(bar);
      }
    } else {
      int result = random.getNextRandom(0, 100);
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

  public void sellFooBar(boolean switchingActivity) {
    List<FooBar> fooBars = supplyDepot.removeFiveFooBar();
    long numberOfFooBar = fooBars.stream().filter(Objects::nonNull).count();
    if (numberOfFooBar != 5) {
      LOGGER.warn("Can't sell foobar, you have only {} foobar on the 5 required.", numberOfFooBar);
      fooBars.forEach(supplyDepot::addFooBar);
    } else {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Sell fooBar: {}.", String.join("," + fooBars.stream().map(FooBar::getSerialNumber).toList()));
      }
      Utils.sleep(SELL_FOO_BAR_TIME, switchingActivity);
      supplyDepot.addMoney(5);
    }
  }

  public void buyRobot(boolean switchingActivity) {
    List<Foo> fooList = supplyDepot.removeFoo(6);
    int balance = supplyDepot.removeMoney(3);
    long numberOfFoo = fooList.stream().filter(Objects::nonNull).count();
    if (numberOfFoo != 6 || balance < 0) {
      if (fooList.size() != 6) {
        LOGGER.warn("Can't buy robot, you have only {} foo on the 6 required.", numberOfFoo);
        fooList.forEach(supplyDepot::addFoo);
      } else {
        LOGGER.warn("Can't buy robot, you have only {}€ on the 3€ required.", balance);
        supplyDepot.addMoney(3);
      }
    } else {
      LOGGER.info("Buying a new robot.");
      Utils.sleep(BUY_ROBOT_TIME, switchingActivity);
      supplyDepot.addRobot(ActivityEnum.MINING_BAR);
    }
  }

}
