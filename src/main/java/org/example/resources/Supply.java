package org.example.resources;

import java.util.UUID;

public abstract class Supply {

    protected final UUID serialNumber;

    protected Supply() {
        this.serialNumber = UUID.randomUUID();
    }

    public final UUID getSerialNumber() {
        return this.serialNumber;
    }

}
