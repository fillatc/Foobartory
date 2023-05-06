package org.example.domain;

import java.util.UUID;

public abstract class Supply {

    protected final UUID serialNumber;

    protected Supply() {
        this.serialNumber = UUID.randomUUID();
    }

}
