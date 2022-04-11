package com.example.sensorBIM.resources.Building;

import com.example.sensorBIM.model.Room;
import com.example.sensorBIM.services.Building.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/room")
public class RoomResource {

    private final RoomService roomService;

    public RoomResource(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Room>> getAllRooms() {
        List<Room> rooms = roomService.findRooms();
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @GetMapping("/all/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable("id") Long id) {
        Room room = roomService.findRoomByID(id);
        return new ResponseEntity<>(room, HttpStatus.OK);
    }

    @GetMapping("/rooms/{levelId}")
    public ResponseEntity<Collection<Room>> getRoomsByLevelId(@PathVariable("levelId") Long levelId) {
        Collection<Room> rooms = roomService.findRoomsForLevel(levelId);
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }
}
