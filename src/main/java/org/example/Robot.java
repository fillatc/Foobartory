package org.example;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.example.domain.ActivityEnum;
import org.example.domain.Bar;
import org.example.domain.Foo;
import org.example.domain.FooBar;
import org.example.utils.RandomSingleton;
import org.example.utils.Utils;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import static org.example.utils.Constants.*;

public class Robot implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(Robot.class);

  private final UUID serialNumber;
  private final SupplyDepot supplyDepot;
  private final RandomSingleton random;
  private ActivityEnum activity;


  public Robot(SupplyDepot supplyDepot, RandomSingleton random, ActivityEnum activity) {
    this.serialNumber = UUID.randomUUID();
    this.supplyDepot = supplyDepot;
    this.random = random;
    this.activity = activity;
    LOGGER.info("New robot, serialNumber: {}", serialNumber);
  }

  @Override
  public void run() {
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
    return switch (this.activity) {
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
      Optional<Pair<Foo, Bar>> opt = supplyDepot.removeFooAndBar();
      if (opt.isPresent()) {
          Foo foo = opt.get().getValue0();
          Bar bar = opt.get().getValue1();
          int result = random.getNextRandom(0, 100);
          Utils.sleep(ASSEMBLE_FOO_BAR_TIME, switchingActivity);
          if (result <= 60) {
              var fooBar = new FooBar();
              LOGGER.debug("Assemble fooBar: {} from foo: {} and bar: {}", fooBar.getSerialNumber(), foo.getSerialNumber(), bar.getSerialNumber());
              supplyDepot.addFooBar(fooBar);
          } else {
              LOGGER.debug("Assembling failed! foo: {} | bar: {}", foo.getSerialNumber(), bar.getSerialNumber());
              supplyDepot.addBar(bar);
          }
      } else {
          LOGGER.warn("Can't assemble foobar !");
      }
  }

  public void sellFooBar(boolean switchingActivity) {
    List<FooBar> fooBars = supplyDepot.removeFooBar(5);

    if (fooBars.isEmpty()) {
      LOGGER.warn("Can't sell foobar, you don't have the 5 fooBar required.");
    } else {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Sell fooBar: {}.",
                fooBars.stream()
                    .map(fooBar -> fooBar.getSerialNumber().toString())
                    .collect(Collectors.joining(", "))
            );
        }
      Utils.sleep(SELL_FOO_BAR_TIME, switchingActivity);
      supplyDepot.addMoney(5);
    }
  }


  public void buyRobot(boolean switchingActivity) {
      Optional<Pair<List<Foo>, Integer>> opt = supplyDepot.removeFooAndMoney(FOO_TO_BUILD_ROBOT, ROBOT_PRICE);
      if (opt.isPresent()) {
          LOGGER.info("Buying a new robot.");
          Utils.sleep(BUY_ROBOT_TIME, switchingActivity);
          supplyDepot.addRobot(ActivityEnum.MINING_BAR);
      } else {
          LOGGER.warn("Can't buy robot !");
      }
  }

}
