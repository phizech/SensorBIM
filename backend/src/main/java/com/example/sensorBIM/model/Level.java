package com.example.sensorBIM.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "Level")
public class Level {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false, name = "ID")
    private long id;

    @Column(name = "Uri")
    @NotNull
    private String uri;

    @Column(name = "Name")
    @NotNull
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "level", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Room> rooms;

    @ManyToOne(optional = false)
    @JoinColumn(name = "Building_ID")
    @JsonBackReference
    private Building building;

    public Level(String uri, String name, Building building) {
        this.uri = uri;
        this.name = name;
        this.building = building;
    }

    public Level() {

    }
}
