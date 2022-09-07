package com.example.sensorBIM.IFC2EnergyPass;


import com.example.sensorBIM.model.Enums.SwitchingDeviceStatus;
import com.example.sensorBIM.model.Room;
import com.example.sensorBIM.model.SwitchingDevice;
import com.example.sensorBIM.services.InfluxDB.InfluxConnectionService;
import com.example.sensorBIM.services.Sensor.SwitchingDeviceService;

public class SimpleController implements Strategy {

    private double setPoint = 0;
    private Room room = null;
    private SwitchingDevice switchingDevice = null;
    private InfluxConnectionService influxConnectionService;

    public SimpleController(int setPoint, Room room, SwitchingDevice switchingDevice, InfluxConnectionService influxConnectionService, SwitchingDeviceService switchingDeviceService) {
        this.setPoint = setPoint;
        this.room = room;
        this.switchingDevice = switchingDevice;
        this.influxConnectionService = influxConnectionService;
    }

    public void execute() throws Exception {

        var currentTemperature = getCurrentTemperature();

        if (currentTemperature < setPoint && switchingDevice.getStatus() == SwitchingDeviceStatus.OFF) {
            System.out.println("Current temperature () below set point () - turning on heater");
            switchingDevice.setStatus(SwitchingDeviceStatus.ON);
        } else if (currentTemperature >= setPoint && switchingDevice.getStatus() == SwitchingDeviceStatus.ON) {
            System.out.println("Current temperature () at or above set point () - turning off heater");
            switchingDevice.setStatus(SwitchingDeviceStatus.OFF);
        }
    }

    private double getCurrentTemperature() {
        var measurement = influxConnectionService.getLatestMeasurementPoint(room, "temperature");
        return (double) measurement.getValue();
    }
}