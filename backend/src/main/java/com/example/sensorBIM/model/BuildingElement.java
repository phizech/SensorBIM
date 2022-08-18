package com.example.sensorBIM.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "BuildingElement")
public class BuildingElement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false, name = "ID")
    private Long id;

    @Column(name = "Uri")
    private String uri;

    @ManyToOne(optional = false)
    @JoinColumn(name = "Room_ID")
    @JsonBackReference
    private Room room;

    @Column(name = "Surface")
    private double surface;

    @Column(name = "Density")
    private double densityPerCMInGrams;

    @Column(name = "Conductance")
    private double conductance;

    @Column(name = "HeatCapacity")
    private double heatCapacity;

}
