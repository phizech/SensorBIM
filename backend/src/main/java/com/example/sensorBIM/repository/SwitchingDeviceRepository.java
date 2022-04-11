package com.example.sensorBIM.repository;

import com.example.sensorBIM.model.SwitchingDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface SwitchingDeviceRepository extends JpaRepository<SwitchingDevice, Long> {

    @Query("SELECT s FROM SwitchingDevice s WHERE s.building.id = :buildingId")
    Collection<SwitchingDevice> findAllSwitchingDevicesForBuilding(Long buildingId);

    @Query("SELECT s FROM SwitchingDevice s WHERE s.ip = :ip AND s.slug = :slug AND s.onPath = :onPath")
    Optional<SwitchingDevice> findSwitchingDeviceByIPAndOnPath(String ip, String slug, String onPath);

    @Query("SELECT s FROM SwitchingDevice s WHERE s.ip = :ip AND s.slug = :slug AND s.offPath = :offPath")
    Optional<SwitchingDevice> findSwitchingDeviceByIPAndOffPath(String ip, String slug, String offPath);

    @Query("SELECT s FROM SwitchingDevice s WHERE s.ip = :ip AND s.slug = :slug AND s.statusPath = :statusPath")
    Optional<SwitchingDevice> findSwitchingDeviceByIPAndStatusPath(String ip, String slug, String statusPath);
}
