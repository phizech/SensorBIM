package com.example.sensorBIM.model;

import com.example.sensorBIM.model.Enums.SwitchingDeviceStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "SwitchingDevice")
public class SwitchingDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false, name = "ID")
    private Long id;

    @Column(name = "Name")
    private String name;

    @Column(name = "Token")
    private String token;

    @Column(name = "IP")
    private String ip;

    @Column(name = "OnPath")
    private String onPath;

    @Column(name = "OffPath")
    private String offPath;

    @Column(name = "StatusPath")
    private String statusPath;

    @Column(name = "Slug")
    private String slug;

    @Column(name = "Status")
    private SwitchingDeviceStatus status;

    @Column(name = "automatic")
    private boolean automatic;

    @Column(name = "comment")
    private String comment;

    @OneToOne
    @JoinColumn(name = "room_id")
    private Room room;
}
