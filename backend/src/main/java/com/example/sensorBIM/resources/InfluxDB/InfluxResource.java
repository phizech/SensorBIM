package com.example.sensorBIM.resources.InfluxDB;

import com.example.sensorBIM.HttpBody.MeasurePoint;
import com.example.sensorBIM.HttpBody.Response;
import com.example.sensorBIM.HttpBody.ResponseStatus;
import com.example.sensorBIM.model.Enums.SensorType;
import com.example.sensorBIM.model.Room;
import com.example.sensorBIM.model.Sensor;
import com.example.sensorBIM.services.Building.RoomService;
import com.example.sensorBIM.services.InfluxDB.InfluxConnectionService;
import com.example.sensorBIM.services.Sensor.SensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/influx")
public class InfluxResource {

    @Autowired
    private InfluxConnectionService influxConnectionService;

    @Autowired
    private SensorService sensorService;

    @Autowired
    private RoomService roomService;

    @GetMapping("/getSensorMeasurements/{levelId}/{roomUri}/{measurement}/{date}")
    public ResponseEntity<List<Response<Map<String, List<MeasurePoint>>>>> getMeasurementPointsForSensors(
            @PathVariable("levelId") Long levelId, @PathVariable("roomUri") String roomUri,
            @PathVariable("measurement") String measurement, @PathVariable("date") String dateFrom) {
        Map<String, List<MeasurePoint>> sensorMeasurements = new HashMap<>();
        List<Sensor> sensors = getSensorsInRoom(levelId, roomUri, measurement);
        List<Response<Map<String, List<MeasurePoint>>>> responses = new ArrayList<>();
        for (Sensor sensor : sensors) {
            if (sensor == null) {
                responses.add(new Response<>(ResponseStatus.FAILURE, "Der Sensor konnte nicht geladen werden.", null));
            } else {
                List<MeasurePoint> measurements = influxConnectionService.queryInfluxDB(measurement, sensor.getRoom().getLevel().getBuilding(), sensor, dateFrom);
                if (measurements == null) {
                    responses.add(new Response<>(ResponseStatus.FAILURE, "Der Sensor mit der ID " + sensor.getName() + " hat in diesem Zeitraum nichts gemessen.", null));
                } else if (!measurements.isEmpty()) {
                    sensorMeasurements.put(sensor.getName() + ", " + sensor.getTransmissionType().getTransferType(), measurements);
                }
            }
        }
        if (!sensorMeasurements.isEmpty()) {
            responses.add(new Response<>(ResponseStatus.SUCCESS, "Sensorwerte wurden erfolgreich geladen.", sensorMeasurements));
        }
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    private List<Sensor> getSensorsInRoom(Long levelId, String roomName, String measurement) {
        return sensorService.findSensorsByLevelIdAndRoomName(levelId, roomName).
                stream().
                filter(s -> isValid(s.getSensorType().getSensorTypeString(), measurement)).collect(Collectors.toList());

    }

    public boolean isValid(String sensorType, String measurement) {
        return (sensorType.toUpperCase().equals(measurement)) || (sensorType.equals(SensorType.MULTI.getSensorTypeString().toUpperCase()));
    }

    @GetMapping("/getLatestSensorMeasurements/{buildingId}/{levelId}/{roomName}/{selectedSensorType}")
    public ResponseEntity<Response<List<MeasurePoint>>> getLatestSensorMeasurementPoints(@PathVariable("buildingId") Long buildingId,@PathVariable("levelId") Long levelId, @PathVariable("roomName") String roomName, @PathVariable("selectedSensorType") String selectedSensorType) {
        Room room = roomService.findRoomByBuildingIdLevelIdAndRoomName(buildingId, levelId, roomName);
        List<MeasurePoint> measurePoints = influxConnectionService.getMeasurementsOfAllSensors(room, selectedSensorType);
        Response<List<MeasurePoint>> response;
        if(measurePoints==null){
            response = new Response<>(ResponseStatus.FAILURE, "Keine neuen Messungen", null);
        } else {
            response = new Response<>(ResponseStatus.SUCCESS, "", measurePoints);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/getLatestSensorMeasurement/{roomId}/{selectedSensorType}")
    public ResponseEntity<Response<MeasurePoint>> getLatestSensorMeasurementPoint(@PathVariable("roomId") Long roomId, @PathVariable("selectedSensorType") String selectedSensorType) {
        Room room = roomService.findRoomByID(roomId);
        Sensor sensor = findSensorWithSelectedSensorType(room, selectedSensorType);
        Response<MeasurePoint> response;
        if (sensor == null) {
            response = new Response<>(ResponseStatus.FAILURE, "Im " + room.getName() + " gibt es keinen Sensor, welcher " + selectedSensorType + " misst.", null);
        } else {
            MeasurePoint measurement = influxConnectionService.getLatestMeasurementPoint(room, selectedSensorType);
            if (measurement == null) {
                response = new Response<>(ResponseStatus.FAILURE, "In den letzten 24h wurden keine Werte gemessen", null);
            } else {
                response = new Response<>(ResponseStatus.SUCCESS, "Die " + selectedSensorType + " im Raum " + sensor.getRoom().getName() + " beträgt " + measurement.getValue() + measurement.getUnit(), measurement);
            }
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private Sensor findSensorWithSelectedSensorType(Room room, String selectedSensorType) {
        for (Sensor s : room.getSensors()) {

            if ((s.getSensorType().getSensorTypeString().equals(selectedSensorType.toLowerCase()))
                    || (s.getSensorType().getSensorTypeString().equals(SensorType.MULTI.getSensorTypeString()))) {
                return s;
            }
        }
        return null;
    }
}
