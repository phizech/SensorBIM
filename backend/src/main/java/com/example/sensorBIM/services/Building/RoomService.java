package com.example.sensorBIM.services.Building;

import com.example.sensorBIM.model.Room;
import com.example.sensorBIM.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Component
@Scope("application")
@Transactional
public class RoomService {

    private final RoomRepository roomRepository;

    @Autowired
    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public Room addRoom(Room room) {
        return roomRepository.save(room);
    }

    public List<Room> findRooms() {
        return roomRepository.findAll();
    }

    public Room findRoomByID(Long id) {
        return roomRepository.findById(id).orElse(null);
    }

    public Collection<Room> findRoomsForLevel(Long levelId) {
        return roomRepository.findAllRoomsForLevel(levelId);
    }

}
