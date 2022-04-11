package com.example.sensorBIM.services.Building;

import com.example.sensorBIM.HttpBody.Response;
import com.example.sensorBIM.HttpBody.ResponseStatus;
import com.example.sensorBIM.model.Building;
import com.example.sensorBIM.repository.BuildingRepository;
import com.example.sensorBIM.services.InfluxDB.InfluxConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

@Component
@Scope("application")
@Transactional
public class BuildingService {
    private final BuildingRepository buildingRepository;

    @Autowired
    private InfluxConnectionService influxConnectionService;

    @Autowired
    public BuildingService(BuildingRepository buildingRepository) {
        this.buildingRepository = buildingRepository;
    }

    public Building addBuilding(Building building) {
        return buildingRepository.save(building);
    }

    public List<Building> findBuildings() {
        return buildingRepository.findAll();
    }

    public Building findBuildingByID(Long id) {
        return buildingRepository.findById(id).orElse(null);
    }

    public Building findBuildingByNameAndUser(String buildingName, String username) {
        return buildingRepository.getByName(buildingName, username);
    }

    public void deleteBuilding(Building building) {
        buildingRepository.delete(building);
    }

    public List<Building> findBuildingsForUser(Long userId) {
        return buildingRepository.findAllBuildingsForUser(userId);
    }

    /**
     * update the building and return true if it was successful
     *
     * @param building the building to update
     * @return true if the building was updated successfully
     */
    public ResponseEntity<Response<Object>> updateBuilding(Building building) {
        String message = isSavingBuildingAllowed(building);
        if (message != null) {
            return new ResponseEntity<>(new Response<>(ResponseStatus.WARNING, message, null), HttpStatus.OK);
        }
        buildingRepository.save(building);
        return new ResponseEntity<>(new Response<>(ResponseStatus.SUCCESS, "building.update.success", building), HttpStatus.OK);

    }

    /**
     * saving the building is only allowed, if the url is valid, meaning we can ping it, and if the name is unique (pre user)
     *
     * @param building the building to save
     * @return returns true if saving the building is allowed
     */
    public String isSavingBuildingAllowed(Building building) {
        if (findBuildingByNameAndUser(building.getName(), building.getUser().getUsername()) != null) {
            if (building.getId() != findBuildingByNameAndUser(building.getName(), building.getUser().getUsername()).getId()) {
                return "building.saving.nameTaken";
            }
        }
        if (!influxConnectionService.testInfluxConnection(building.getInfluxDatabaseUrl())) {
            return "building.saving.wrongURL";
        }
        return null;
    }
}
