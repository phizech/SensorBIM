package com.example.sensorBIM.services.InfluxDB;

import com.example.sensorBIM.HttpBody.MeasurePoint;
import com.example.sensorBIM.model.Building;
import com.example.sensorBIM.model.Enums.TransmissionType;
import com.example.sensorBIM.model.Room;
import com.example.sensorBIM.model.Sensor;
import com.example.sensorBIM.repository.SensorRepository;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Pong;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Scope("application")
public class InfluxConnectionService {

    @Autowired
    private SensorRepository sensorRepository;

    /**
     * tests whether the entered url of the building is valid. for that we try to ping it, if we get a response it
     * is valid, if not, we return false, indicating the url is not vald
     *
     * @param databaseURL the url of the building
     * @return true if the url is valid, false if not
     */
    public boolean testInfluxConnection(String databaseURL) {
        try {
            final InfluxDB influxDB = InfluxDBFactory.connect(databaseURL);
            Pong response = influxDB.ping();
            if (response.getVersion().equalsIgnoreCase("unknown")) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * create the influx client. with this client we can send requests, like queries to get the sensor data
     *
     * @param databaseUrl      the url of the building
     * @param token            the token with the permission
     * @param organizationName the name of the organization (in influx)
     * @param bucketName       the name of the bucket containing the sensor data (in influx)
     * @return returns the client of influxdb
     */
    public InfluxDBClient createInfluxClient(String databaseUrl, char[] token, String organizationName, String bucketName) {
        return InfluxDBClientFactory.create(databaseUrl, token, organizationName, bucketName);
    }

    /**
     * send a request to influxdb to get a given measurement of a given building.
     *
     * @param measurement the measurement, e.g. temperature or humidity
     * @param building    the building of which we want to get the measurements
     * @param sensor      the sensor of which we want to get the measurements
     * @param dateFrom    the date from which we want to get the measurements: from dateFrom until today
     * @return a list of all the measurements or null, if there are not measurements
     */
    public List<MeasurePoint> queryInfluxDB(String measurement, Building building, Sensor sensor, String dateFrom) {
        try {
            InfluxDBClient client = createInfluxClient(building.getInfluxDatabaseUrl(), building.getInfluxDBToken().toCharArray(), building.getOrganizationName(), sensor.getBucketName());
            String fluxQuery = "from(bucket: \"" + sensor.getBucketName() + "\")\n" +
                    "  |> range(start: " + dateFrom + ")\n" +
                    "  |> filter(fn: (r) => r[\"_measurement\"] == \"" + measurement.toLowerCase() + "\")\n" +
                    "  |> filter(fn: (r) => r[\"" + getIdentifierInInflux(sensor) + "\"] == \"" + String.valueOf(sensor.getInfluxIdentifier()) + "\")\n" +
                    "  |> filter(fn: (r) => r[\"_field\"] == \"value\")" +
                    "  |> yield(name: \"mean\")";
            QueryApi queryApi = client.getQueryApi();
            List<FluxTable> res = queryApi.query(fluxQuery);
            if (res.isEmpty()) {
                return null;
            }
            return getData(res.get(0).getRecords());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * extract the data from the records and save them to a measure point
     *
     * @param records the records, returned by influxdb
     * @return a list of measure points containing information about measurements like sensor name and type, time of measurements, etc.
     */
    private List<MeasurePoint> getData(List<FluxRecord> records) {
        List<MeasurePoint> measurements = new ArrayList<>();
        for (FluxRecord record : records) {
            Sensor sensor = getSensorFromLatestRecord(record);
            measurements.add(
                    new MeasurePoint(
                            sensor.getName(),
                            sensor.getTransmissionType().getTransferType(),
                            record.getTime(),
                            record.getStart(),
                            record.getStop(),
                            record.getMeasurement(),
                            record.getValue(),
                            record.getValueByKey("unit"))
            );
        }
        return measurements;
    }

    /**
     * get the sensor of the latest measurement
     *
     * @param record the record returned by influxdb
     * @return the sensor to which the record belongs to
     */
    private Sensor getSensorFromLatestRecord(FluxRecord record) {
        String identifier;
        if (record.getValueByKey("epc") != null) {
            identifier = Objects.requireNonNull(record.getValueByKey("epc")).toString();
        } else {
            identifier = Objects.requireNonNull(record.getValueByKey("hardware_id")).toString();
        }
        return sensorRepository.findSensorByInfluxIdentifier(identifier.toCharArray()).stream().findFirst().get();
    }

    /**
     * get the latest measurement of a given room. this method is called for the 2d view and will define the color
     *
     * @param room        the room of which we want to get the latest measurements
     * @param measurement the measurement, e.g. temperature or humidity
     * @return the latest measure point
     */
    public MeasurePoint getLatestMeasurementPoint(Room room, String measurement) {
        try {
            Building building = room.getLevel().getBuilding();
            InfluxDBClient client = createInfluxClient(building.getInfluxDatabaseUrl(),
                    building.getInfluxDBToken().toCharArray(),
                    building.getOrganizationName(),
                    room.getSensors().iterator().next().getBucketName());
            String fluxQuery = getFluxQuery(room, measurement);
            QueryApi queryApi = client.getQueryApi();
            List<FluxTable> res = queryApi.query(fluxQuery);
            if (res.isEmpty()) {
                return null;
            }
            return getData(res.get(0).getRecords()).get(0);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * if we want to get the latest measurements of the room, we also need all the sensors contained in the room
     * we create a query to get all the data of all the sensors
     *
     * @param room        the room of which we want to get the latest measurements
     * @param measurement the measurement, e.g. humidity or temperature
     * @return the created flux query
     */
    public String getFluxQuery(Room room, String measurement) {
        String query = "";
        List<String> tables = new ArrayList<>();
        for (Map.Entry<String, String> entry : getSensorIdsOfSensorsInRoom(room).entrySet()) {
            tables.add(entry.getKey());
            query += entry.getKey() + " = from(bucket: \"" + entry.getKey() + "\")\n" +
                    "  |> range(start: -48h)\n" +
                    "  |> filter(fn: (r) => r[\"_measurement\"] == \"" + measurement.toLowerCase() + "\")\n" +
                    "  |> filter(fn: (r) => " + entry.getValue() + ")\n" +
                    "  |> last() \n";
        }
        if (tables.size() > 1) {
            query += "union(tables: [" + String.join(", ", tables) + "])\n" +
                    "  |> filter(fn: (r) => r[\"_field\"] == \"value\")  \n" +
                    "  |> filter(fn: (r) => r[\"valid\"] == \"True\")\n" +
                    "|> group()";
        } else {
            query += tables.get(0);
        }
        return query;
    }

    /**
     * gt all the sensor ids of the sensors contained in a given room
     *
     * @param room th room of which we want to get the information
     * @return a map containing th ids of the sensors and the bucket name
     */
    public Map<String, String> getSensorIdsOfSensorsInRoom(Room room) {
        Map<String, String> sensorPerBucket = new HashMap<>();
        String toAdd;
        String key;
        for (Sensor s : room.getSensors()) {
            key = s.getBucketName();
            toAdd = "r[\"" + getIdentifierInInflux(s) + "\"] == \"" + String.valueOf(s.getInfluxIdentifier()) + "\"";
            if (sensorPerBucket.get(key) == null) {
                sensorPerBucket.put(key, toAdd);
            } else {
                sensorPerBucket.put(key, sensorPerBucket.get(key) + " or " + toAdd);
            }
        }
        return sensorPerBucket;
    }

    /**
     * the sensor has, depending on if it is cable bound or not, an id: either epc or a hardware id
     *
     * @param sensor the sensor of which we want to gete the id
     * @return a string, either epc or hardware_id; empty if not valid
     */
    public String getIdentifierInInflux(Sensor sensor) {
        if (sensor.getTransmissionType() == TransmissionType.RDIF) {
            return "epc";
        } else if (sensor.getTransmissionType() == TransmissionType.CABLE_BOUND) {
            return "hardware_id";
        }
        return "";
    }
}
