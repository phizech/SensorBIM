package com.example.sensorBIM.resources.building;

import com.example.sensorBIM.model.Level;
import com.example.sensorBIM.resources.Building.LevelResource;
import com.example.sensorBIM.services.Building.LevelService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Collection;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class LevelResourceTest {

    @Autowired
    private LevelResource levelResource;

    @Autowired
    private LevelService levelService;

    @Test
    public void testGetLevelById() {
        Long buildingId = 1L;
        Collection<Level> levels = levelResource.getLevelByBuildingId(buildingId).getBody();
        Assert.assertNotNull(levels);
        Assert.assertFalse(levels.isEmpty());
        Assert.assertEquals(levelService.findLevelsByBuildingId(buildingId).size(), levels.size());
    }

    @Test
    public void testGetAllLevels() {
        Assert.assertEquals(levelService.findLevels().size(), levelResource.getAllLevels().getBody().size());
    }
}
