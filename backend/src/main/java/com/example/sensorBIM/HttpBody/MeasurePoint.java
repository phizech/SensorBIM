package com.example.sensorBIM.HttpBody;

import lombok.Getter;

import java.time.Instant;

@Getter
public class MeasurePoint {
    private final String sensorName;
    private final String sensorType;
    private final Instant time;
    private final Instant startTime;
    private final Instant stopTime;
    private final String measurement;
    private final Object value;
    private final Object unit;

    public MeasurePoint(String sensorName, String sensorType, Instant time, Instant startTime, Instant stopTime, String measurement, Object value, Object unit) {
        this.sensorName = sensorName;
        this.sensorType = sensorType;
        this.time = time;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.measurement = measurement;
        this.value = value;
        this.unit = getUnitAsString((String) unit);
    }

    private String getUnitAsString(String unit) {
        if(unit.contains("temp_celsius")){
            return unit.replace("temp_celsius", "°C");
        }
        if(unit.contains("rel_hum")){
            return unit.replace("rel_hum", "%rH");
        }
        return unit;
    }
}
