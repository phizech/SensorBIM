package com.example.sensorBIM.services;

import com.example.sensorBIM.model.Building;
import com.example.sensorBIM.model.User;
import com.example.sensorBIM.services.Building.BuildingService;
import com.example.sensorBIM.services.User.UserService;
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

import java.util.HashSet;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BuildingServiceTest {

    private static Long NUMBER_OF_BUILDINGS;
    @Autowired
    private BuildingService buildingService;

    @Autowired
    private UserService userService;

    @Before
    public void init() {
        NUMBER_OF_BUILDINGS = (long) buildingService.findBuildings().size();
    }

    @Test
    public void testFindBuildingByName() {
        String buildingName = "Testgebäude";
        Building building = buildingService.findBuildingByNameAndUser(buildingName, "steverogers");
        Assert.assertNotNull(building);
        Assert.assertEquals(buildingName, building.getName());
    }

    @Test
    public void testFindAllBuildings() {
        Assert.assertEquals(NUMBER_OF_BUILDINGS.intValue(), buildingService.findBuildings().size());
    }

    @Test
    public void testFindBuildingsForUser() {
        User user = userService.findUserByUsername("peterparker");
        Assert.assertNotNull(user);
        Assert.assertNotNull(user.getBuildings());
        List<Building> buildings = buildingService.findBuildingsForUser(user.getId());
        Assert.assertNotNull(buildings);
        Assert.assertEquals(0, buildings.size());
    }

    @Test
    public void testFindBuildingByID() {
        for (Long i = 1L; i <= NUMBER_OF_BUILDINGS; i++) {
            Assert.assertNotNull(buildingService.findBuildingByID(i));
        }
    }


    @Test
    public void testAddBuilding() {
        int currentNrOfBuildings = buildingService.findBuildings().size();
        Building building = new Building();
        building.setName("New Test Building");
        User user = userService.findUsers().get(0);
        building.setUser(user);
        building.setInfluxDatabaseUrl("");
        building.setOrganizationName("");
        building.setInfluxDBToken("");
        building.setLevels(new HashSet<>());
        building = buildingService.addBuilding(building);
        Assert.assertNotNull(building);
        Assert.assertNotNull(building.getLevels());
        Assert.assertEquals(user, building.getUser());
        Assert.assertEquals(currentNrOfBuildings + 1, buildingService.findBuildings().size());
        NUMBER_OF_BUILDINGS++;
    }

    @Test
    public void updateBuilding() {
        Building building = buildingService.findBuildings().get(0);
        String buildingName = building.getName();
        String newName = "This is a new name";
        building.setName(newName);
        buildingService.updateBuilding(building);
        Assert.assertNotEquals(buildingName, building.getName());
        Assert.assertEquals(newName, building.getName());
    }

    @Test
    public void saveAlreadyExistingBuilding() {
        Building building = buildingService.findBuildings().get(0);
        building.setId(Integer.MAX_VALUE);
        String response = buildingService.isSavingBuildingAllowed(building);
        Assert.assertNotNull(response);
        Assert.assertEquals("building.saving.nameTaken", response);
    }

    @Test
    public void testDeleteBuilding() {
        Building toDelete = buildingService.findBuildingByNameAndUser("Testgebäude 2", "steverogers");
        Assert.assertNotNull(toDelete);
        buildingService.deleteBuilding(toDelete);
        Assert.assertFalse(buildingService.findBuildings().contains(toDelete));
        NUMBER_OF_BUILDINGS--;
    }

}
