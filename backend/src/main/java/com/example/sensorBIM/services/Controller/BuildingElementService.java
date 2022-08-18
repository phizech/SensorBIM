package com.example.sensorBIM.services.Controller;

import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Scope("application")
@RequestMapping("/config")
public class BuildingElementService {

    public Double getDensityFromMaterial(String material){
        if(material.contains("Zellulose")){
            return 1.5;
        }
        if(material.contains("Beton")){
            return 2.4;
        }
        if(material.contains("Holz")){
            return 1.5;
        }
        if(material.contains("Bitumen")){
            return 1.5;
        }
        if(material.contains("Ziegel")){
            return 5.0;
        }
        return 2.0; // welcher default wert sollen wir nehemn?
    }

}
