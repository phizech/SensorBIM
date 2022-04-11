package com.example.sensorBIM.repository;

import com.example.sensorBIM.model.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {

    @Query("SELECT s FROM Sensor s WHERE s.room.id = :roomId")
    Collection<Sensor> findAllSensorsForRoom(@Param("roomId") Long roomId);

    @Query("SELECT s FROM Sensor s WHERE s.room.level.id = :levelId AND s.room.name = :roomName")
    Collection<Sensor> findAllSensorsForRoomName(Long levelId, String roomName);

    @Query("SELECT s FROM Sensor s WHERE s.influxIdentifier = :identifier")
    Collection<Sensor> findSensorByInfluxIdentifier(char[] identifier);

    @Query("SELECT s FROM Sensor s WHERE s.influxIdentifier = :identifier AND s.room.id = :roomId")
    Sensor findSensorByInfluxIdentifierAndRoomId(char[] identifier, Long roomId);
}
