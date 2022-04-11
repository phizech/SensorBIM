package com.example.sensorBIM.resources.building;

import com.example.sensorBIM.model.Room;
import com.example.sensorBIM.resources.Building.RoomResource;
import com.example.sensorBIM.services.Building.RoomService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Collection;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class RoomResourceTest {

    @Autowired
    private RoomResource roomResource;

    @Autowired
    private RoomService roomService;

    @Test
    public void testGetAllRooms() {
        List<Room> rooms = roomResource.getAllRooms().getBody();
        Assert.assertNotNull(rooms);
        Assert.assertFalse(rooms.isEmpty());
        Assert.assertEquals(roomService.findRooms().size(), rooms.size());
    }

    @Test
    public void testGetRoomsByLevelId() {
        Long levelId = 1L;
        Collection<Room> rooms = roomResource.getRoomsByLevelId(1L).getBody();
        Assert.assertNotNull(rooms);
        Assert.assertFalse(rooms.isEmpty());
        Assert.assertEquals(roomService.findRoomsForLevel(levelId).size(), rooms.size());
    }

    @Test
    public void testGetRoomById() {
        int numberOfRooms = roomResource.getAllRooms().getBody().size();
        for (int i = 1; i <= numberOfRooms; i++) {
            Assert.assertNotNull(roomResource.getRoomById((long) i));
        }
    }
}
