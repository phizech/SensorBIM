package com.example.sensorBIM.HttpBody;

public enum ResponseStatus {
    SUCCESS("SUCCESS", "success"),
    INFO("INOF", "info"),
    FAILURE("FAILURE", "failure"),
    WARNING("WARNING", "warning");

    ResponseStatus(String name, String label) {
    }
}