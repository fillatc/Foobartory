package org.example.domain;

import java.util.UUID;

public class FooBar {
  private final UUID serialNumber;

  public FooBar() {
    this.serialNumber = UUID.randomUUID();
  }

  public UUID getSerialNumber() {
    return serialNumber;
  }
}