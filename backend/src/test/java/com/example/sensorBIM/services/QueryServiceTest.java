package com.example.sensorBIM.services;

import com.example.sensorBIM.HttpBody.Response;
import com.example.sensorBIM.HttpBody.ResponseStatus;
import com.example.sensorBIM.TestConstants;
import com.example.sensorBIM.model.Building;
import com.example.sensorBIM.model.User;
import com.example.sensorBIM.services.Building.BuildingService;
import com.example.sensorBIM.services.Building.LevelService;
import com.example.sensorBIM.services.Building.RoomService;
import com.example.sensorBIM.services.Configuration.QueryService;
import com.example.sensorBIM.services.Sensor.SensorService;
import com.example.sensorBIM.services.User.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class QueryServiceTest {

    @Autowired
    private QueryService queryService;

    @Autowired
    private UserService userService;

    @Autowired
    private BuildingService buildingService;

    @Autowired
    private LevelService levelService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private SensorService sensorService;


    @Test
    public void testLoadValidTTL() {
        User user = userService.findUserByUsername("natasharomanoff");
        Assert.assertNotNull(user);
        Building building = getBuilding("validBuilding", user);
        validateOutput(1, 2, 3, 7,
                user, building, TestConstants.OUTPUT_TTL,
                "upload.success", ResponseStatus.SUCCESS);
    }

    @Test
    public void testLoadInvalidTTL() {
        User user = userService.findUserByUsername("natasharomanoff");
        Assert.assertNotNull(user);
        Building building = getBuilding("InvalidTTLBuilding", user);
        validateOutput(0, 0, 0, 0,
                user, building, TestConstants.INVALID_TTL,
                "upload.file.invalidTurtle", ResponseStatus.FAILURE);
    }

    @Test
    public void testLoadTTLWithInvalidInfluxCredentials() {
        User user = userService.findUserByUsername("natasharomanoff");
        Assert.assertNotNull(user);
        Building building = new Building();
        building.setName("InvalidInfluxCreds");
        building.setInfluxDatabaseUrl(TestConstants.INFLUX_URL);
        building.setOrganizationName(TestConstants.ORGANIZATION_NAME);
        building.setInfluxDBToken("testToken");
        building.setUser(user);
        validateOutput(0, 0, 0, 0,
                user, building, TestConstants.INVALID_TTL,
                "upload.file.invalidTurtle", ResponseStatus.FAILURE);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testLoadTTLWithInvalidBuilding() {
        User user = userService.findUserByUsername("natasharomanoff");
        Assert.assertNotNull(user);
        Building building = new Building();
        validateOutput(0, 0, 0, 0,
                user, building, TestConstants.OUTPUT_TTL,
                "Die Datei ist keine gültige Turtle Datei!", ResponseStatus.FAILURE);
    }

    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void testLoadTTLWithInvalidUser() {
        User user = new User();
        Building building = getBuilding("Invalid user", user);
        validateOutput(0, 0, 0, 0,
                user, building, TestConstants.OUTPUT_TTL,
                "Die Datei ist keine gültige Turtle Datei!", ResponseStatus.FAILURE);
    }

    public Building getBuilding(String buildingName, User user) {
        Building building = new Building();
        building.setName(buildingName);
        building.setOrganizationName(TestConstants.ORGANIZATION_NAME);
        building.setInfluxDBToken(TestConstants.INFLUX_TOKEN);
        building.setInfluxDatabaseUrl(TestConstants.INFLUX_URL);
        building.setUser(user);
        return building;
    }

    public void validateOutput(int newBuildings, int newLevels, int newRooms, int newSensors,
                               User user, Building building, String ttlFile, String expectedMessage, ResponseStatus expectedResponseStatus) {

        int numberOfBuildings = buildingService.findBuildings().size();
        int numberOfBuildingsForUser = buildingService.findBuildingsForUser(user.getId()).size();
        int numberOfLevels = levelService.findLevels().size();
        int numberOfRooms = roomService.findRooms().size();
        int numberOfSensors = sensorService.findSensors().size();

        Response<Object> errorResponse = queryService.loadIFCOWL2Database(building, ttlFile);

        Assert.assertEquals(numberOfBuildingsForUser + newBuildings, buildingService.findBuildingsForUser(user.getId()).size());
        Assert.assertEquals(expectedMessage, errorResponse.getMessage());
        Assert.assertEquals(expectedResponseStatus, errorResponse.getResponseStatus());
        Assert.assertEquals(numberOfBuildings + newBuildings, buildingService.findBuildings().size());
        Assert.assertEquals(numberOfLevels + newLevels, levelService.findLevels().size());
        Assert.assertEquals(numberOfRooms + newRooms, roomService.findRooms().size());
        Assert.assertEquals(numberOfSensors + newSensors, sensorService.findSensors().size());
    }
}
