package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.example.domain.ActivityEnum;
import org.example.domain.Bar;
import org.example.domain.Foo;
import org.example.domain.FooBar;
import org.example.utils.RandomSingleton;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class FoobartoryTest {

  private final RandomSingleton random = RandomSingleton.getInstance();

  @Test
  public void mineFooTest() {
    //GIVEN
    var supplyDepot = new SupplyDepot();
    var robot = new Robot(supplyDepot, random, ActivityEnum.MINING_FOO);
    
    //WHEN
    robot.mineFoo(false);

    //THEN
    assertEquals(1, supplyDepot.getTotalFoo());
  }

  @Test
  public void mineBarTest() {
    //GIVEN
    var supplyDepot = new SupplyDepot();
    var robot = new Robot(supplyDepot, random, ActivityEnum.MINING_BAR);

    //WHEN
    robot.mineBar(false);

    //THEN
    assertEquals(1, supplyDepot.getTotalBar());
  }

  @Test
  public void assembleFooBarTest() {
    // GIVEN
    var supplyDepot = new SupplyDepot();
    supplyDepot.addBar(new Bar());
    supplyDepot.addFoo(new Foo());
    RandomSingleton random = Mockito.mock(RandomSingleton.class);
    when(random.getNextRandom(0, 100)).thenReturn(1);

    var robot = new Robot(supplyDepot, random, ActivityEnum.ASSEMBLING_FOO_BAR);
   
    // WHEN
    robot.assembleFooBar(false);

    // THEN
    assertEquals(0, supplyDepot.getTotalBar());
    assertEquals(0, supplyDepot.getTotalFoo());
    assertEquals(1, supplyDepot.getTotalFooBar());
  }

  @Test
  public void assembleFooBarTest_with_failed_assembling() {
    // GIVEN
    var supplyDepot = new SupplyDepot();
    supplyDepot.addBar(new Bar());
    supplyDepot.addFoo(new Foo());
    RandomSingleton random = Mockito.mock(RandomSingleton.class);
    when(random.getNextRandom(0, 100)).thenReturn(70);

    var robot = new Robot(supplyDepot, random, ActivityEnum.ASSEMBLING_FOO_BAR);
   
    // WHEN
    robot.assembleFooBar(false);

    // THEN
    assertEquals(1, supplyDepot.getTotalBar());
    assertEquals(0, supplyDepot.getTotalFoo());
    assertEquals(0, supplyDepot.getTotalFooBar());
  }

  @Test
  public void sellFooBarTest() {
    // GIVEN
    var supplyDepot = new SupplyDepot();
    for (var counter = 0; counter < 5; counter ++) {
      supplyDepot.addFooBar(new FooBar());
    }
    assertTrue(supplyDepot.getTotalFooBar() == 5);
    var robot = new Robot(supplyDepot, random, ActivityEnum.SELLING_FOO_BAR);
   
    // WHEN
    robot.sellFooBar(false);

    // THEN
    assertEquals(0, supplyDepot.getTotalFooBar());
    assertEquals(5, supplyDepot.getTotalMoney());
  }

  @Test
  public void buyRobotTest() {
    // GIVEN
    var supplyDepot = new SupplyDepot();
    for (var counter = 0; counter < 6; counter ++) {
      supplyDepot.addFoo(new Foo());
    }
    assertTrue(supplyDepot.getTotalFoo() == 6);
    supplyDepot.addMoney(3);
    var robot = new Robot(supplyDepot, random, ActivityEnum.BUYING_ROBOT);

     // WHEN
     robot.buyRobot(false);

     // THEN
     assertEquals(0, supplyDepot.getTotalFoo());
     assertEquals(0, supplyDepot.getTotalMoney());
     assertEquals(1, supplyDepot.getTotalRobot());
     assertTrue(supplyDepot.removeRobot() != null);
  }
  
}
