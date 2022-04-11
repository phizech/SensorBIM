package com.example.sensorBIM.model;

import com.example.sensorBIM.model.Enums.SensorType;
import com.example.sensorBIM.model.Enums.TransmissionType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "Sensor")
public class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false, name = "ID")
    private Long id;

    @Column(name = "influxIdentifier")
    private char[] influxIdentifier;

    @Column(name = "Name")
    private String name;

    @Column(name = "Uri")
    private String uri;

    @ManyToOne(optional = false)
    @JoinColumn(name = "Room_ID")
    @JsonBackReference
    private Room room;

    @Column(name = "MaxValue")
    private double maxValue;

    @Column(name = "MinValue")
    private double minValue;

    @Column(name = "SensorType")
    private SensorType sensorType;

    @Column(name = "TransmissionType")
    private TransmissionType transmissionType;

    @Column(name = "SamplingRate")
    private double samplingRate;

    @Column(name = "bucketName")
    private String bucketName;

}
