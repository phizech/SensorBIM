package com.example.sensorBIM.services.Building;

import com.example.sensorBIM.model.Level;
import com.example.sensorBIM.repository.LevelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;

@Component
@Scope("application")
@Transactional
public class LevelService {
    private final LevelRepository levelRepository;

    @Autowired
    public LevelService(LevelRepository levelRepository) {
        this.levelRepository = levelRepository;
    }

    public Level addLevel(Level level) {
        return levelRepository.save(level);
    }

    public List<Level> findLevels() {
        return levelRepository.findAll();
    }

    public Level findLevelByID(Long id) {
        return levelRepository.findById(id).orElse(null);
    }

    public Collection<Level> findLevelsByBuildingId(Long id) {
        return levelRepository.findAllLevelsForBuilding(id);
    }

}
