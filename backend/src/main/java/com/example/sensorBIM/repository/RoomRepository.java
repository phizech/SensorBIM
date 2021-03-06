package com.example.sensorBIM.repository;

import com.example.sensorBIM.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("SELECT r FROM Room r WHERE r.level.id = :levelId")
    Collection<Room> findAllRoomsForLevel(@Param("levelId") Long levelId);

    @Query("SELECT r FROM Room r WHERE r.level.id = :levelId AND r.level.building.id = :buildingId AND r.name = :roomName")
    Room findRoomByBuildingIdLevelIdAndRoomName(@Param("buildingId") Long buildingId, @Param("levelId")  Long levelId, @Param("roomName")  String roomName);
}
