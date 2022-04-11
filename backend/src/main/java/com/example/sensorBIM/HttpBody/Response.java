package com.example.sensorBIM.HttpBody;

import lombok.Getter;

@Getter
public class Response<T> {

    private final ResponseStatus responseStatus;
    private final String message;
    private final T body;

    public Response(ResponseStatus responseStatus, String message, T body) {
        this.responseStatus = responseStatus;
        this.message = message;
        this.body = body;
    }
}
