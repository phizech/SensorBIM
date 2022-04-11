package com.example.sensorBIM.services.Configuration;

import be.ugent.IfcSpfReader;
import org.apache.jena.graph.Graph;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.graph.GraphFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
@Scope("application")
public class Ifc2IcfOWLConverterService {

    /**
     * this method converts the ifc file to ifcowl
     * the converter used here was created
     *
     * @param upload the path to the uploaded file
     * @param output the path to the output file
     * @return return true if no exception was thrown
     */
    public boolean convertIFCtoIFCOWL(String upload, String output) throws IOException {
        IfcSpfReader reader = new IfcSpfReader();
        Graph expected = GraphFactory.createGraphMem();
        RDFDataMgr.read(expected, new FileInputStream(new File(output).getAbsolutePath()), Lang.TTL);
        reader.setup(new File(upload).getAbsolutePath());
        reader.convert(upload, output, "http://linkedbuildingdata.net/ifc/resources/");
        return true;
    }
}
