package com.example.sensorBIM.resources.Building;

import com.example.sensorBIM.model.Level;
import com.example.sensorBIM.services.Building.LevelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/level")
public class LevelResource {

    private final LevelService levelService;

    public LevelResource(LevelService levelService) {
        this.levelService = levelService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Level>> getAllLevels() {
        List<Level> levels = levelService.findLevels();
        return new ResponseEntity<>(levels, HttpStatus.OK);
    }

    @GetMapping("/all/{id}")
    public ResponseEntity<Collection<Level>> getLevelByBuildingId(@PathVariable("id") Long id) {
        Collection<Level> level = levelService.findLevelsByBuildingId(id);
        return new ResponseEntity<>(level, HttpStatus.OK);
    }

}
