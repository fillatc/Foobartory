package org.example;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.example.domain.Bar;
import org.example.domain.Foo;
import org.example.domain.FooBar;

public class SupplyDepot {

  private final AtomicBoolean running = new AtomicBoolean(true);
  private final AtomicInteger totalRobot = new AtomicInteger(0);
  private final AtomicInteger money = new AtomicInteger(0);
  private final Queue<Foo> fooQueue = new LinkedList<>();
  private final Queue<Bar> barQueue = new LinkedList<>();
  private final Queue<FooBar> fooBarQueue = new LinkedList<>();
  private final Queue<Robot> robotIdle = new LinkedList<>();


  public void stop() {
    synchronized (running) {
      running.getAndSet(false);
    }
  }

  public boolean isRunning() {
    synchronized (running) {
      return running.get();
    }
  }

  public void addFoo(Foo foo) {
    synchronized (barQueue) {
      fooQueue.add(foo);
    }
  }

  public Foo removeFoo() {
    synchronized (this) {
      return fooQueue.poll();
    }
  }
  
  public List<Foo> removeFoo(int nb) {
    synchronized (this) {
      List<Foo> fooList = new ArrayList<>();
      for (var cpt = 0; cpt < nb; cpt++) {
        fooList.add(fooQueue.poll());
      }
      return fooList;
    }
  }

   public int getTotalFoo() {
    synchronized (barQueue) {
      return fooQueue.size();
    }
  }

  public void addBar(Bar bar) {
    synchronized (barQueue) {
      barQueue.add(bar);
    }
  }

  public Bar removeBar() {
    synchronized (this) {
      return barQueue.poll();
    }
  }
  public int getTotalBar() {
    synchronized (barQueue) {
      return barQueue.size();
    }
  }

  public void addFooBar(FooBar fooBar) {
    synchronized (fooBarQueue) {
      fooBarQueue.add(fooBar);
    }
  }

  public List<FooBar> removeFooBar() {
    synchronized (fooBarQueue) {
      List<FooBar> fooBars = new ArrayList<>();
      for (var cpt = 0; cpt < 5; cpt++) {
        fooBars.add(fooBarQueue.poll());
      }
      return fooBars;
    }
  }

  public int getTotalFooBar() {
    synchronized (this) {
      return fooBarQueue.size();
    }
  }

  public int getTotalRobot() {
    synchronized (totalRobot) {
      return totalRobot.get();
    }
  }

  public void addRobot() {
    synchronized (totalRobot) {
      totalRobot.getAndIncrement();
    }
  }

}
