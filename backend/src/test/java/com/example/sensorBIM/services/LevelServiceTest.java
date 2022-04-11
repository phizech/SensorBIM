package com.example.sensorBIM.services;

import com.example.sensorBIM.model.Level;
import com.example.sensorBIM.services.Building.BuildingService;
import com.example.sensorBIM.services.Building.LevelService;
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

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LevelServiceTest {

    private static Long NUMBER_OF_LEVELS;
    @Autowired
    private BuildingService buildingService;

    @Autowired
    private LevelService levelService;

    @Before
    public void init() {
        NUMBER_OF_LEVELS = (long) levelService.findLevels().size();
    }

    @Test
    public void testFindAllLevels() {
        Assert.assertEquals(NUMBER_OF_LEVELS.intValue(), levelService.findLevels().size());
    }

    @Test
    public void testFindLevelById() {
        for (Long i = 1L; i < NUMBER_OF_LEVELS; i++) {
            Assert.assertNotNull(levelService.findLevelByID(i));
        }
    }

    @Test
    public void testAddLevel() {
        Level level = new Level();
        level.setBuilding(buildingService.findBuildings().get(0));
        level.setName("New Test Level");
        level.setUri("this is a uri");
        level.setRooms(new HashSet<>());
        level = levelService.addLevel(level);
        Assert.assertNotNull(level);
        Assert.assertNotNull(level.getRooms());
        Assert.assertNotNull(level.getName());
        NUMBER_OF_LEVELS++;
    }
}
