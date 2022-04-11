package com.example.sensorBIM.services;

import com.example.sensorBIM.model.Room;
import com.example.sensorBIM.services.Building.LevelService;
import com.example.sensorBIM.services.Building.RoomService;
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
public class RoomServiceTest {

    private static Long NUMBER_OF_ROOMS;

    @Autowired
    private LevelService levelService;

    @Autowired
    private RoomService roomService;

    @Before
    public void init() {
        NUMBER_OF_ROOMS = (long) levelService.findLevels().size();
    }

    @Test
    public void testFindRoomById() {
        for (Long i = 1L; i < NUMBER_OF_ROOMS; i++) {
            Assert.assertNotNull(roomService.findRoomByID(i));
        }
    }

    @Test
    public void testAddRoom() {
        Room room = new Room();
        room.setLevel(levelService.findLevels().get(0));
        room.setName("New Test Room");
        room.setUri("this is a uri");
        room.setGeometry("[[6.57292264691303, 4.3845272421737], [11.812922646913, 4.3845272421737], [11.812922646913, 8.51452724217372], [6.57292264691303, 8.51452724217372], [6.57292264691303, 4.3845272421737]]");
        room.setSensors(new HashSet<>());
        room = roomService.addRoom(room);
        Assert.assertNotNull(room);
        Assert.assertNotNull(room.getGeometry());
        Assert.assertNotNull(room.getSensors());
        NUMBER_OF_ROOMS++;
    }
}
