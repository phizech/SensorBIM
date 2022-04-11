package com.example.sensorBIM.resources.building;

import com.example.sensorBIM.model.Building;
import com.example.sensorBIM.model.User;
import com.example.sensorBIM.resources.Building.BuildingResource;
import com.example.sensorBIM.services.Building.BuildingService;
import com.example.sensorBIM.services.User.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;
import java.util.Objects;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class BuildingResourceTest {

    @Autowired
    private BuildingResource buildingResource;

    @Autowired
    private BuildingService buildingService;

    @Autowired
    private UserService userService;

    @Test
    public void testGetAllBuildings() {
        User user = userService.findUserByUsername("steverogers");
        Assert.assertNotNull(user);
        List<Building> buildings = buildingResource.getAllBuildingsForUser(user.getId()).getBody();
        Assert.assertNotNull(buildings);
        Assert.assertFalse(buildings.isEmpty());
        Assert.assertFalse(buildingService.findBuildingsForUser(user.getId()).isEmpty());
        Assert.assertEquals(buildingService.findBuildingsForUser(user.getId()).size(), buildings.size());
    }

    @Test
    public void testDeleteBuilding() {
        User user = userService.findUserByUsername("steverogers");
        Assert.assertNotNull(user);
        Building building = Objects.requireNonNull(buildingResource.getAllBuildingsForUser(user.getId()).getBody()).get(0);
        Assert.assertNotNull(building);
        buildingResource.deleteBuilding(building);
        Assert.assertFalse(buildingService.findBuildings().contains(building));
        Assert.assertFalse(Objects.requireNonNull(buildingResource.getAllBuildingsForUser(user.getId()).getBody()).contains(building));
    }
}
