package com.example.sensorBIM.services.InfluxDB;

import com.example.sensorBIM.model.Building;
import com.example.sensorBIM.model.Enums.SensorType;
import com.example.sensorBIM.model.Sensor;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("application")
public class InfluxService {

    @Autowired
    private InfluxConnectionService influxConnectionService;

    /**
     * before saving the sensor to the database, we want to check whether the sensor is in influxdb
     *
     * @param building the building to which the sensor belongs; this is needed for the token and url
     * @param sensor   the sensor
     * @return true if the sensor is contained in influx (if the sensor measured something in the last 30 days)
     */
    public boolean isSensorInInflux(Building building, Sensor sensor) {
        try {
            InfluxDBClient client = influxConnectionService.createInfluxClient(building.getInfluxDatabaseUrl(), building.getInfluxDBToken().toCharArray(), building.getOrganizationName(), sensor.getBucketName());
            String fluxQuery = "from(bucket: \"" + sensor.getBucketName() + "\")\n" +
                    "  |> range(start: -30d)\n" +
                    "  |> filter(fn: (r) => r[\"_measurement\"] == \"" + getType(sensor).toString().toLowerCase() + "\")\n" +
                    "  |> filter(fn: (r) => r[\"_field\"] == \"value\")" +
                    "  |> filter(fn: (r) => r[\"" + influxConnectionService.getIdentifierInInflux(sensor) + "\"] == \"" + String.valueOf(sensor.getInfluxIdentifier()) + "\")\n" +
                    "  |> yield(name: \"mean\")";
            QueryApi queryApi = client.getQueryApi();
            queryApi.query(fluxQuery);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * if the type of the sensor is multi, we just check whether the sensor measured the humidity
     *
     * @param sensor the sensor to check
     * @return the sensor type
     */
    public SensorType getType(Sensor sensor) {
        if (sensor.getSensorType().equals(SensorType.MULTI)) {
            return SensorType.HUMIDITY;
        }
        return sensor.getSensorType();
    }

}
