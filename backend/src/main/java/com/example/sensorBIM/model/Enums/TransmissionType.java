package com.example.sensorBIM.model.Enums;

import lombok.Getter;

public enum TransmissionType {
    RDIF("RFID"),
    CABLE_BOUND("Kabelgebunden");

    @Getter
    private final String transferType;

    TransmissionType(String transferType) {
        this.transferType = transferType;
    }
}

