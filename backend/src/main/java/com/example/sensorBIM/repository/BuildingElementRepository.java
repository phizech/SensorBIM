package com.example.sensorBIM.repository;

import com.example.sensorBIM.model.Building;
import com.example.sensorBIM.model.BuildingElement;
import org.springframework.data.jpa.repository.Query;

public interface BuildingElementRepository {

    @Query("SELECT b FROM BuildingElement b WHERE b.uri = :buildingElementName")
    BuildingElement getByUri(String buildingElementName);
}
