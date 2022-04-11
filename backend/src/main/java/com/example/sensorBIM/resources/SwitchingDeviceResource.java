package com.example.sensorBIM.resources;

import com.example.sensorBIM.HttpBody.SwitchingDeviceResult;
import com.example.sensorBIM.model.SwitchingDevice;
import com.example.sensorBIM.services.Sensor.SwitchingDeviceService;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collection;


@RestController
@RequestMapping("/switchingDevice")
public class SwitchingDeviceResource {

    private final SwitchingDeviceService switchingDeviceService;

    public SwitchingDeviceResource(SwitchingDeviceService switchingDeviceService) {
        this.switchingDeviceService = switchingDeviceService;
    }

    @GetMapping("/{buildingId}")
    public ResponseEntity<Collection<SwitchingDeviceResult>> getAllSwitchingDevicesByBuilding(@PathVariable("buildingId") Long buildingId) {
        Collection<SwitchingDeviceResult> switchingDevices = switchingDeviceService.findSwitchingDevicesByBuildingId(buildingId);
        return new ResponseEntity<>(switchingDevices, HttpStatus.OK);
    }

    @GetMapping("/status/{switchingDeviceId}")
    public ResponseEntity<SwitchingDeviceResult> getStatusOfSwitchingDevice(@PathVariable("switchingDeviceId") Long id) throws IOException, JSONException {
        SwitchingDevice device = switchingDeviceService.findSwitchingDeviceById(id);
        return new ResponseEntity<>(switchingDeviceService.getStatusOfSwitchingDevice(device), HttpStatus.OK);
    }

    @PostMapping("/switch")
    public ResponseEntity<SwitchingDeviceResult> setSwitchingDeviceStatus(@RequestBody SwitchingDevice device) throws IOException, JSONException {
        return new ResponseEntity<>(switchingDeviceService.setSwitchingDeviceStatus(device), HttpStatus.OK);
    }

    @GetMapping("/switchMode/{switchingDeviceId}/{automatic}")
    public ResponseEntity<SwitchingDeviceResult> switchMode(@PathVariable("switchingDeviceId") Long id, @PathVariable("automatic") boolean automatic) throws IOException, InterruptedException, JSONException {
        SwitchingDevice device = switchingDeviceService.findSwitchingDeviceById(id);
        return new ResponseEntity<>(switchingDeviceService.switchMode(device, automatic), HttpStatus.OK);
    }
}
