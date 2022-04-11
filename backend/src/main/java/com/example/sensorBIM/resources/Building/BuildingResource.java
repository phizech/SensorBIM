package com.example.sensorBIM.resources.Building;

import com.example.sensorBIM.model.Building;
import com.example.sensorBIM.services.Building.BuildingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/building")
public class BuildingResource {

    private final BuildingService buildingService;

    public BuildingResource(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    @GetMapping("/allForUser/{userId}")
    public ResponseEntity<List<Building>> getAllBuildingsForUser(@PathVariable("userId") Long userId) {
        List<Building> buildings = buildingService.findBuildingsForUser(userId);
        return new ResponseEntity<>(buildings, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Building>> getAllBuildings() {
        List<Building> buildings = buildingService.findBuildings();
        return new ResponseEntity<>(buildings, HttpStatus.OK);
    }

    @PostMapping("/delete")
    public ResponseEntity<Boolean> deleteBuilding(@RequestBody Building building) {
        buildingService.deleteBuilding(building);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @PostMapping("/update")
    public ResponseEntity<Object> updateBuilding(@RequestBody Building building) {
        return new ResponseEntity<>(buildingService.updateBuilding(building), HttpStatus.OK);
    }

    @GetMapping("/byId/{buildingId}")
    public ResponseEntity<Building> getBuildingById(@PathVariable("buildingId") Long buildingId) {
        Building building = buildingService.findBuildingByID(buildingId);
        return new ResponseEntity<>(building, HttpStatus.OK);
    }
}
