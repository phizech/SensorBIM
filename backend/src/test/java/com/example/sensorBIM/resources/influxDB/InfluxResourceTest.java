package com.example.sensorBIM.resources.influxDB;

import com.example.sensorBIM.HttpBody.MeasurePoint;
import com.example.sensorBIM.HttpBody.Response;
import com.example.sensorBIM.HttpBody.ResponseStatus;
import com.example.sensorBIM.model.Room;
import com.example.sensorBIM.resources.InfluxDB.InfluxResource;
import com.example.sensorBIM.services.Building.RoomService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class InfluxResourceTest {

    @Autowired
    private InfluxResource influxResource;

    @Autowired
    private RoomService roomService;

    @Test
    public void testGetLatestSensorMeasurementPoints() {
        Room room = roomService.findRoomByID(1L);
        if (roomContainsSensors(room)) {
            Response<MeasurePoint> response = influxResource.getLatestSensorMeasurementPoint(room.getId(), "TEMPERATURE").getBody();
            Assert.assertNotNull(response);
            MeasurePoint measurePoint = response.getBody();
            Assert.assertNotNull(measurePoint.getMeasurement());
            Assert.assertNotNull(measurePoint.getSensorType());
            Assert.assertNotNull(measurePoint.getSensorName());
            Assert.assertNotNull(measurePoint.getStartTime());
            Assert.assertNotNull(measurePoint.getStopTime());
            Assert.assertNotNull(measurePoint.getTime());
            Assert.assertNotNull(measurePoint.getValue());
            Assert.assertEquals(ResponseStatus.SUCCESS, response.getResponseStatus());
            Assert.assertNotNull(response.getBody());
        }
    }

    @Test
    public void testGetLatestSensorMeasurementPointsWithInvalidSensorType() {
        Room room = roomService.findRoomByID(1L);
        if (roomContainsSensors(room)) {
            Response<MeasurePoint> response = influxResource.getLatestSensorMeasurementPoint(room.getId(), "some type").getBody();
            assert response != null;
            Assert.assertEquals(ResponseStatus.FAILURE, response.getResponseStatus());
            Assert.assertNull(response.getBody());
        }
    }

    @Test
    public void testGetMeasurementPointsForSensors() {
        Room room = roomService.findRooms().get(0);
        if (roomContainsSensors(room)) {
            ResponseEntity<List<Response<Map<String, List<MeasurePoint>>>>> responses = influxResource.getMeasurementPointsForSensors(room.getLevel().getId(), room.getName(), "HUMIDITY", "2021-12-29");
            Assert.assertNotNull(responses);
            Assert.assertNotNull(responses.getBody());
            Assert.assertFalse(responses.getBody().isEmpty());
            Assert.assertEquals(ResponseStatus.SUCCESS, responses.getBody().get(0).getResponseStatus());
            Assert.assertNotNull(responses.getBody().get(0).getBody());
        }
    }

    @Test
    public void testGetMeasurementPointsForSensorsWithInvalidSensor() {
        Room room = roomService.findRoomByID(9L);
        if (roomContainsSensors(room)) {
            ResponseEntity<List<Response<Map<String, List<MeasurePoint>>>>> responses = influxResource.getMeasurementPointsForSensors(room.getLevel().getId(), room.getUri(), "HUMIDITY", "2021-12-29");
            Assert.assertNotNull(responses);
            Assert.assertNotNull(responses.getBody());
            Assert.assertTrue(responses.getBody().isEmpty());
            Assert.assertEquals(0, responses.getBody().size());
        }
    }

    public boolean roomContainsSensors(Room room) {
        if (room != null) {
            if (room.getSensors() != null) {
                return !room.getSensors().isEmpty();
            }
        }
        return false;
    }
}
