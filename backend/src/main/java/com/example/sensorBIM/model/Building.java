package com.example.sensorBIM.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "Building")
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "ID")
    private long id;

    @Column(nullable = false, name = "Name")
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "building", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonManagedReference
    private Set<Level> levels;

    @Column(nullable = false, name = "influxToken")
    private String influxDBToken;

    @Column(nullable = false, name = "influxDatabaseUrl")
    private String influxDatabaseUrl;

    @Column(nullable = false, name = "organizationName")
    private String organizationName;

    @ManyToOne(optional = false)
    @JoinColumn(name = "User_ID")
    private User user;


    public Building() {

    }

}
