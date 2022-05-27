package org.example.domain;

import java.util.UUID;

public class Bar {
  private final UUID serialNumber;

  public Bar() {
    this.serialNumber = UUID.randomUUID();
  }

  public UUID getSerialNumber() {
    return serialNumber;
  }
}
