package com.example.sensorBIM.services.Sensor;

import com.example.sensorBIM.model.Sensor;
import com.example.sensorBIM.repository.SensorRepository;
import com.example.sensorBIM.services.InfluxDB.InfluxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Component
@Scope("application")
@Transactional
public class SensorService {

    @Autowired
    private SensorRepository sensorRepository;

    @Autowired
    private InfluxService influxService;

    public Sensor addSensor(Sensor sensor) {
        if (isSavingSensorAllowed(sensor)) {
            return sensorRepository.save(sensor);
        }
        return null;
    }

    public boolean isSavingSensorAllowed(Sensor sensor) {
        return isSensorValid(sensor) && isSensorInInfluxDB(sensor) && isSensorUnique(sensor);
    }

    private boolean isSensorUnique(Sensor sensor) {
        return sensorRepository.findSensorByInfluxIdentifierAndRoomId(sensor.getInfluxIdentifier(), sensor.getRoom().getId()) == null;
    }

    private boolean isSensorInInfluxDB(Sensor sensor) {
        return influxService.isSensorInInflux(sensor.getRoom().getLevel().getBuilding(), sensor);
    }

    private boolean isSensorValid(Sensor sensor) {
        return (sensor.getSensorType() != null) &&
                (sensor.getInfluxIdentifier() != null) &&
                (sensor.getBucketName() != null) &&
                (sensor.getRoom() != null) &&
                (sensor.getMaxValue() != sensor.getMinValue()) &&
                (!(sensor.getMaxValue() < sensor.getMinValue())) &&
                (sensor.getName() != null) &&
                (sensor.getTransmissionType() != null);
    }

    public List<Sensor> findSensors() {
        return sensorRepository.findAll();
    }

    public Collection<Sensor> findSensorsByRoomId(Long id) {
        return sensorRepository.findAllSensorsForRoom(id);
    }

    public Collection<Sensor> findSensorsByLevelIdAndRoomName(Long levelId, String roomName) {
        return sensorRepository.findAllSensorsForRoomName(levelId, roomName);
    }
}
