package com.example.sensorBIM;

import java.nio.file.Paths;

public class TestConstants {
    public static final String UPLOAD_FILE_DIRECTORY = String.valueOf(Paths.get("src/test/resources/data/testIFC.ifc").toAbsolutePath());
    public static final String OUTPUT_TTL = String.valueOf(Paths.get("src/test/resources/data/testIFC.ttl").toAbsolutePath());
    public static final String INVALID_TTL = String.valueOf(Paths.get("src/test/resources/data/invalidIFC.ttl").toAbsolutePath());
    public static final String link = "http://localhost:4200";
    public static final String INFLUX_URL = "http://qe-sensorbim.uibk.ac.at:8086/";
    public static final String ORGANIZATION_NAME = "Quality Engineering";
    public static final String INFLUX_TOKEN = "-0oyIPOsLHrNfenULtHU2QTgsBiMvIPBY143fDefzgxrXc7slyugil8kwaFt1wQ1qX1QBuRkEBJZ-jPWrssvxQ==";
}
