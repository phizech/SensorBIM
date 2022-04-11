package com.example.sensorBIM.repository;

import com.example.sensorBIM.model.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BuildingRepository extends JpaRepository<Building, Long> {

    @Query("SELECT b FROM Building b WHERE b.name = :buildingName AND b.user.username = :username")
    Building getByName(String buildingName, String username);

    @Query("SELECT b FROM Building b WHERE b.user.id = :userId")
    List<Building> findAllBuildingsForUser(Long userId);
}
