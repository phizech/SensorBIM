package com.example.sensorBIM.services;

import java.nio.file.Paths;

public class Constants {
    public static final String UPLOAD_FILE_DIRECTORY = String.valueOf(Paths.get("uploadedIFC.ifc").toAbsolutePath());
    public static final String OUTPUT_TTL = String.valueOf(Paths.get("uploadedIFC.ttl").toAbsolutePath());
    public static final String link = "http://localhost:4200";

}
