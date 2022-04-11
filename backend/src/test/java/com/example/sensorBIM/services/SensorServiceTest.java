package com.example.sensorBIM.services;

import com.example.sensorBIM.model.Enums.SensorType;
import com.example.sensorBIM.model.Enums.TransmissionType;
import com.example.sensorBIM.model.Room;
import com.example.sensorBIM.model.Sensor;
import com.example.sensorBIM.services.Building.RoomService;
import com.example.sensorBIM.services.Sensor.SensorService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SensorServiceTest {

    private static Long NUMBER_OF_SENSORS;

    @Autowired
    private RoomService roomService;

    @Autowired
    private SensorService sensorService;

    @Before
    public void init() {
        NUMBER_OF_SENSORS = (long) sensorService.findSensors().size();
    }

    @Test
    public void testFindAllSensors() {
        Assert.assertEquals(NUMBER_OF_SENSORS.intValue(), sensorService.findSensors().size());
    }

    @Test
    public void testGetAllSensorsByRoomId() {
        Room room = roomService.findRoomByID(9L);
        Assert.assertNotNull(room);
        Assert.assertEquals(6, sensorService.findSensorsByRoomId(room.getId()).size());
    }

    @Test
    public void testValidAddSensor() {
        Sensor sensor = new Sensor();
        Room room = roomService.findRooms().get(2);
        Assert.assertNotNull(room);
        sensor.setRoom(room);
        sensor.setName("Sensor_01:Sensor:123");
        sensor.setUri("http://linkedbuildingdata.net/ifc/resources20211214_124501/IfcBuildingElementProxy_123");
        sensor.setSensorType(SensorType.HUMIDITY);
        sensor.setBucketName("scanntronic");
        sensor.setInfluxIdentifier("Thermofox".toCharArray());
        sensor.setMaxValue(80);
        sensor.setMinValue(-80);
        sensor.setSamplingRate(30);
        sensor.setTransmissionType(TransmissionType.CABLE_BOUND);
        sensor = sensorService.addSensor(sensor);
        Assert.assertNotNull(sensor);
        Assert.assertNotNull(sensor.getId());
        Assert.assertNotNull(sensor.getUri());
        Assert.assertNotNull(sensor.getSamplingRate());
        NUMBER_OF_SENSORS++;
    }

    @Test
    public void testInvalidAddSensor() {
        Sensor sensor = new Sensor();
        List<Long> roomNames = new ArrayList<>();
        for (Room room : roomService.findRooms()) {
            roomNames.add(room.getId());
        }
        Assert.assertNotNull(roomNames);
        Room room = roomService.findRooms().get(2);
        Assert.assertNotNull(room);
        sensor.setRoom(room);
        sensor.setName("New Test Sensor");
        sensor.setUri("this is a uri");
        sensor.setSensorType(SensorType.HUMIDITY);
        sensor.setBucketName("cable bound");
        sensor.setInfluxIdentifier("Identifier".toCharArray());
        sensor.setMaxValue(80);
        sensor.setMinValue(-80);
        sensor.setSamplingRate(30);
        sensor.setTransmissionType(TransmissionType.CABLE_BOUND);
        sensor = sensorService.addSensor(sensor);
        Assert.assertNull(sensor);
    }
}
