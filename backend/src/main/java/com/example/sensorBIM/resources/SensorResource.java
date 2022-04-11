package com.example.sensorBIM.resources;

import com.example.sensorBIM.model.Sensor;
import com.example.sensorBIM.services.Sensor.SensorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/sensor")
public class SensorResource {

    private final SensorService sensorService;

    public SensorResource(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @GetMapping("/{levelId}/{roomName}")
    public ResponseEntity<Collection<Sensor>> getAllSensorsByLevelIdAndRoomName(@PathVariable("levelId") Long levelId, @PathVariable("roomName") String roomName) {
        Collection<Sensor> sensor = sensorService.findSensorsByLevelIdAndRoomName(levelId, roomName);
        return new ResponseEntity<>(sensor, HttpStatus.OK);
    }
}
