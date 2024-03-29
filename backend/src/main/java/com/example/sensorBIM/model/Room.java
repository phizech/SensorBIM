package com.example.sensorBIM.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "Room")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false, name = "ID")
    private long id;

    @Column(nullable = false, name = "Name")
    private String name;

    @Column(nullable = false, name = "Uri")
    private String uri;

    @Column(nullable = false, name = "ThermalMass")
    private Double thermalMass;

    @Column(nullable = false, name = "airExchangeRate")
    private Double airExchangeRate;

    @Column(nullable = false, name = "targetTemperature")
    private Double targetTemperature;

    @Column(nullable = false, name = "volume")
    private Double roomVolume;
    @Column(columnDefinition = "TEXT", nullable = false, name = "Geometry")
    private String geometry;

    @ManyToOne
    @JoinColumn(name = "level_id")
    private Level level;

    @OneToMany(mappedBy = "room", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonManagedReference
    private Set<Sensor> sensors;

    @OneToMany(mappedBy = "room", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonManagedReference
    private Set<BuildingElement> relatedBuildingElement;

    @OneToMany(mappedBy = "room", cascade = CascadeType.PERSIST)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonManagedReference
    private Set<SwitchingDevice> switchingDevices;

    public Room(String name, String uri, String geometry, Level level) {
        this.name = name;
        this.uri = uri;
        this.geometry = geometry;
        this.level = level;
        this.switchingDevices = new HashSet<>();
    }

    public Room() {
        this.switchingDevices = new HashSet<>();
    }

}
