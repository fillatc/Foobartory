package org.example.domain;

import java.util.UUID;

public class Foo {
  private final UUID serialNumber;

  public Foo() {
    this.serialNumber = UUID.randomUUID();
  }

  public UUID getSerialNumber() {
    return serialNumber;
  }
}
