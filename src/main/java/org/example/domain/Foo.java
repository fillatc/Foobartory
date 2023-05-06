package org.example.domain;

import java.util.UUID;

public class Foo extends Supply {

  public Foo() {
      super();
  }

  public UUID getSerialNumber() {
    return this.serialNumber;
  }
}
