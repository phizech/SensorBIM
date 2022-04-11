package com.example.sensorBIM.repository;

import com.example.sensorBIM.model.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface LevelRepository extends JpaRepository<Level, Long> {

    @Query("SELECT l FROM Level l WHERE l.building.id = :buildingId")
    Collection<Level> findAllLevelsForBuilding(@Param("buildingId") Long buildingId);
}
