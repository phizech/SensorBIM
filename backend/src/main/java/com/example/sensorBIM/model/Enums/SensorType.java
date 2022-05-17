package com.example.sensorBIM.model.Enums;

import lombok.Getter;

public enum SensorType {
    TEMPERATURE("temperature"),
    HUMIDITY("humidity"),
    MATERIAL_HUMIDITY("material_humidity"),
    PRESSURE("pressure"),
    LIGHT("light"),
    MULTI("multi");

    @Getter
    private final String sensorTypeString;

    SensorType(String sensorType) {
        this.sensorTypeString = sensorType;
    }
}
