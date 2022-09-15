package com.example.sensorBIM.IFC2EnergyPass;


import com.example.sensorBIM.model.Enums.SwitchingDeviceStatus;
import com.example.sensorBIM.model.Room;
import com.example.sensorBIM.model.SwitchingDevice;
import com.example.sensorBIM.services.InfluxDB.InfluxConnectionService;
import com.example.sensorBIM.services.Sensor.SwitchingDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

public class SimpleController implements Strategy {

    private SwitchingDeviceService switchingDeviceService;

    private double setPoint = 0;
    private Room room = null;
    private SwitchingDevice switchingDevice = null;
    private InfluxConnectionService influxConnectionService;

    public SimpleController(Double setPoint, Room room, SwitchingDevice switchingDevice, InfluxConnectionService influxConnectionService, SwitchingDeviceService switchingDeviceService) {
        this.setPoint = setPoint;
        this.room = room;
        this.switchingDevice = switchingDevice;
        this.influxConnectionService = influxConnectionService;
        this.switchingDeviceService = switchingDeviceService;
    }

    public void execute() throws Exception {

        var currentTemperature = getCurrentTemperature();

        if (currentTemperature < setPoint && !switchingDeviceService.getStatusOfSwitchingDevice(switchingDevice).isActive()) {
            System.out.println("Current temperature () below set point () - turning on heater");
            switchingDeviceService.setSwitchingDeviceStatus(switchingDevice);
            // switchingDevice.setStatus(SwitchingDeviceStatus.ON);
        } else if (currentTemperature >= setPoint && switchingDeviceService.getStatusOfSwitchingDevice(switchingDevice).isActive()) {
            System.out.println("Current temperature () at or above set point () - turning off heater");
            switchingDeviceService.setSwitchingDeviceStatus(switchingDevice);
            // switchingDevice.setStatus(SwitchingDeviceStatus.OFF);
        }
    }

    private double getCurrentTemperature() {
        var measurement = influxConnectionService.getLatestMeasurementPoint(room, "Temperature");
        return (double) measurement.getValue();
    }
}