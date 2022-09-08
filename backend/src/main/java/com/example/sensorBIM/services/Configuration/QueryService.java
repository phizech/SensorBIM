package com.example.sensorBIM.services.Configuration;

import com.example.sensorBIM.HttpBody.Response;
import com.example.sensorBIM.HttpBody.ResponseStatus;
import com.example.sensorBIM.IFC2EnergyPass.SimpleController;
import com.example.sensorBIM.model.*;
import com.example.sensorBIM.model.Enums.SensorType;
import com.example.sensorBIM.model.Enums.TransmissionType;
import com.example.sensorBIM.services.Building.BuildingService;
import com.example.sensorBIM.services.Controller.BuildingElementService;
import com.example.sensorBIM.services.InfluxDB.InfluxConnectionService;
import com.example.sensorBIM.services.Sensor.SensorService;
import com.example.sensorBIM.services.Sensor.SwitchingDeviceService;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.FileInputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Query the uploaded ifcowl and save the information to mysql
 */
@Component
@Scope("application")
@Transactional
public class QueryService {

    @Autowired
    private BuildingService buildingService;

    @Autowired
    private SensorService sensorService;

    @Autowired
    private SwitchingDeviceService switchingDeviceService;

    @Autowired
    private BuildingElementService buildingElementService;

    @Autowired
    private InfluxConnectionService influxConnectionService;

    private Dataset dataset;

    private String errorMessage;

    /**
     * save the uploaded turtle file to the dataset. this way we can query the ontology
     *
     * @param outputTTL the turtle file to query
     */
    public void loadTTL(String outputTTL) {
        Model model = ModelFactory.createDefaultModel();
        try {
            model.read(new FileInputStream(outputTTL), "http://ex.org/", "TURTLE");
            this.dataset = DatasetFactory.create();
            dataset.setDefaultModel(model);
        } catch (Exception e) {
            System.out.println("exception occurred: " + e.getMessage());
        }
    }

    /**
     * read the data from the dataset. save the information of the building.
     * if the building is not valid, it is not saved. however, if the building does not
     * contain sensors it still will be saved
     *
     * @param building  the building to save
     * @param outputTTL the turtle file with the information
     * @return return a response object, this indicates whether saving the building was successful or not
     */
    public Response<Object> loadIFCOWL2Database(Building building, String outputTTL) {
        dataset = null;
        building.setLevels(new HashSet<>());
        loadTTL(outputTTL);
        errorMessage = null;
        int numberOfSensors = sensorService.findSensors().size();
        ResultSet levels = getLevels();
        if (dataset.isEmpty()) {
            errorMessage = "upload.file.invalidTurtle";
        } else if (levels.hasNext()) {
            if (buildingService.addBuilding(building) != null) {
                Building finalBuilding = building;
                levels.forEachRemaining(l -> saveLevelsToDB(l, finalBuilding));
            } else {
                errorMessage = "upload.failure";
            }

        } else {
            errorMessage = "upload.building.noStoreys";
        }
        if (errorMessage == null) {
            if (numberOfSensors == sensorService.findSensors().size()) {
                return new Response<>(ResponseStatus.INFO, "upload.building.noSensors", null);
            }
            User user = building.getUser();
            if (user.getBuildings() == null) {
                user.setBuildings(new HashSet<>());
            }
            buildingService.addBuilding(building);
            activateControllerForAllSwitchingDevicesInBuilding(building);
            return new Response<>(ResponseStatus.SUCCESS, "upload.success", null);
        }
        if(buildingService.findBuildingByNameAndUser(building.getName(), building.getUser().getUsername())!=null){
            buildingService.deleteBuilding(building);
        }
        return new Response<>(ResponseStatus.FAILURE, errorMessage, null);
    }

    private void activateControllerForAllSwitchingDevicesInBuilding(Building building) {
        building.getLevels().forEach(l -> l.getRooms().forEach(this::activateController));
    }

    private void activateController(Room room){
        for (SwitchingDevice switchingDevice : room.getSwitchingDevices()){
            if(switchingDevice != null){
                SimpleController controller = new SimpleController(room.getTargetTemperature(), room, switchingDevice, influxConnectionService);
                Timer timer = new Timer();

                timer.scheduleAtFixedRate(new TimerTask() {
                    public void run() {
                        try {
                            controller.execute();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, 1000, 60);
            }
        }


    }

    /**
     * save all the levels to the database
     *
     * @param l        the query solution containing the information
     * @param building the building the levels belong to
     */
    public void saveLevelsToDB(QuerySolution l, Building building) {
        Level newLevel = new Level(l.get("uri").toString(), l.get("name").toString(), building);
        newLevel.setRooms(new HashSet<>());
        building.getLevels().add(newLevel);
        ResultSet rooms = getRooms(newLevel.getUri());
        if (!rooms.hasNext()) {
            errorMessage = "upload.building.noRooms";
            return;
        }
        rooms.forEachRemaining(room -> saveRoomsToDB(room, newLevel));
    }

    /**
     * save all the rooms to the database
     *
     * @param room  the query solution containing the information
     * @param level the level the room belongs to
     */
    public void saveRoomsToDB(QuerySolution room, Level level) {
        Room newRoom = new Room(room.get("name").toString(), room.get("space").toString(), convertToCoordinates(room.get("geometry2d").toString()).toString(), level);
        newRoom.setSensors(new HashSet<>());
        level.getRooms().add(newRoom);

        // add sensors to room
        ResultSet sensorsInRoom = getSensorsWithinRoom(newRoom.getUri());
        sensorsInRoom.forEachRemaining(sensor -> saveSensorsToDB(sensor, newRoom));

        // add sensors, which are inside of walls, to room
        ResultSet sensorsWithinBuildingElement = getSensorsWithinBuildingElements(newRoom.getUri());
        sensorsWithinBuildingElement.forEachRemaining(sensor -> saveSensorsToDB(sensor, newRoom));

        // add related element to room
        ResultSet relatedElements = getRelatedElementsOfSpace(newRoom.getUri());
        relatedElements.forEachRemaining(relatedElement -> saveBuildingElementToDB(relatedElement, newRoom));

        // add information, e.g. volume, to room
        ResultSet roomInformation = getElementInformation(newRoom.getUri());
        setRoomInformation(newRoom, roomInformation);

        // add switching device to room
        ResultSet switchingDevices = getSwitchingDevice(newRoom.getUri());
        switchingDevices.forEachRemaining(device -> saveSwitchingDevicesToDB(device, newRoom));
    }

    private void setRoomInformation(Room newRoom, ResultSet roomInformation) {
        while (roomInformation.hasNext()) {
            QuerySolution q = roomInformation.next();
            String label = q.get("id_label").toString();
            try {
                switch (label) {
                    case "Luftaustauschrate":
                        newRoom.setAirExchangeRate(q.get("doubleValue").asLiteral().getDouble());
                        break;
                    case "Volumen":
                        newRoom.setRoomVolume(q.get("doubleValue").asLiteral().getDouble());
                        break;
                    case "Thermische Masse":
                        newRoom.setThermalMass(q.get("doubleValue").asLiteral().getDouble());
                        break;
                    case "Solltemperatur":
                        newRoom.setTargetTemperature(q.get("doubleValue").asLiteral().getDouble());
                        break;
                    default:
                        break;
                }
            } catch (NullPointerException np) {
                // sensor does contain null literals

            }
        }
    }

    /**
     * save all the sensors to the database
     *
     * @param sensor the query solution containing the information
     * @param room   the room the sensor belongs to
     */
    public void saveSensorsToDB(QuerySolution sensor, Room room) {
        if ((sensor == null) || room == null) {
            return;
        }
        String sensorUri = sensor.get("proxy").toString();
        Sensor newSensor = new Sensor();
        newSensor.setUri(sensorUri);
        newSensor.setRoom(room);
        ResultSet sensorInformation = getElementInformation(sensorUri);
        setSensorInformation(newSensor, sensorInformation);
        if (sensorService.isSavingSensorAllowed(newSensor)) {
            room.getSensors().add(newSensor);
        }
    }

    /**
     * save all the switching devices to the database
     *
     * @param solution the query solution containing the information
     * @param room the room the switching devices belong to
     */
    public void saveSwitchingDevicesToDB(QuerySolution solution, Room room) {
        SwitchingDevice switchingDevice = new SwitchingDevice();
        switchingDevice.setName(solution.get("proxy_name").toString());
        ResultSet deviceInformation = getBuildingElementProxyInformation(solution.get("proxy").toString());
        setSwitchingDeviceInformation(switchingDevice, deviceInformation);
        if (switchingDeviceService.isSavingSwitchingDeviceAllowed(switchingDevice)) {
            switchingDevice.setRoom(room);
            room.getSwitchingDevices().add(switchingDevice);
        }
    }

    /**
     * save all the building elements to the database
     *
     * @param buildingElementQuery the query solution containing the information
     * @param room   the room the sensor belongs to
     */
    public void saveBuildingElementToDB(QuerySolution buildingElementQuery, Room room) {
        if ((buildingElementQuery == null) || room == null) {
            return;
        }
        BuildingElement buildingElement = new BuildingElement();
        buildingElement.setRoom(room);
        buildingElement.setUri(buildingElementQuery.get("relatedSpace").toString());
        ResultSet buildingElementInformation = getParameterValues(buildingElement.getUri());
        setBuildingElementInformation(buildingElement, buildingElementInformation);
        if(room.getRelatedBuildingElement() == null){
            room.setRelatedBuildingElement(new HashSet<>());
        }
        room.getRelatedBuildingElement().add(buildingElement);
    }

    public ResultSet getLevels() {
        return QueryExecutionFactory
                .create("PREFIX ifc: <http://standards.buildingsmart.org/IFC/DEV/IFC2x3/TC1/OWL#>\n" +
                        "PREFIX express: <https://w3id.org/express#>\n" +
                        "select ?uri ?name where {\n" +
                        "    ?uri a ifc:IfcBuildingStorey.\n" +
                        "    ?uri ifc:longName_IfcSpatialStructureElement ?ifcRoot .\n" +
                        "    ?ifcRoot express:hasString ?name .\n" +
                        "} ORDER BY ASC(?uri)", this.dataset)
                .execSelect();
    }

    /**
     * extract the information about the rooms, including the geometry
     *
     * @param levelUri the level uri
     * @return all the rooms in the level with the levelUri
     */
    public ResultSet getRooms(String levelUri) {
        String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
                "PREFIX ifcowl: <http://ifcowl.openbimstandards.org/IFC2X3_TC1#> \n" +
                "PREFIX express: <https://w3id.org/express#> \n" +
                "PREFIX list: <https://w3id.org/list#> \n" +
                "PREFIX ifc: <http://standards.buildingsmart.org/IFC/DEV/IFC2x3/TC1/OWL#>\n" +
                "SELECT ?name ?space (GROUP_CONCAT(CONCAT(\"[\",STR(?x_Value), \", \", STR(?y_Value), \"]\") ; SEPARATOR = \", \" ) AS ?geometry2d) { \n" +
                "    SELECT * where { \n" +
                "        ?spaceBoundary rdf:type ifc:IfcRelSpaceBoundary . \n" +
                "        ?spaceBoundary ifc:relatingSpace_IfcRelSpaceBoundary ?space . \n" +
                "        ?agr rdf:type ifc:IfcRelAggregates . \n" +
                "        ?agr ifc:relatingObject_IfcRelDecomposes <" + levelUri + "> . \n" +
                "        ?agr ifc:relatedObjects_IfcRelDecomposes ?space . \n" +
                "        ?spaceBoundary ifc:relatedBuildingElement_IfcRelSpaceBoundary ?type . \n" +
                "        ?type rdf:type ifc:IfcWallStandardCase . \n" +
                "        ?space ifc:longName_IfcSpatialStructureElement ?label . \n" +
                "        ?spaceBoundary rdf:type  ifc:IfcRelSpaceBoundary . \n" +
                "        ?spaceBoundary ifc:relatingSpace_IfcRelSpaceBoundary ?space . \n" +
                "        ?space ifc:longName_IfcSpatialStructureElement ?label . \n" +
                "        ?label express:hasString ?name . \n" +
                "        ?spaceBoundary ifc:connectionGeometry_IfcRelSpaceBoundary ?connectionSurface . \n" +
                "        ?connectionSurface ifc:surfaceOnRelatingElement_IfcConnectionSurfaceGeometry ?surface . \n" +
                "        ?surface ifc:sweptCurve_IfcSweptSurface ?openProfileDef . \n" +
                "        ?openProfileDef ifc:curve_IfcArbitraryOpenProfileDef ?polyline . \n" +
                "        ?polyline ifc:points_IfcPolyline ?cartesianPoint . \n" +
                "        ?cartesianPoint list:hasContents ?cartesianPoint_x . \n" +
                "        ?cartesianPoint_x ifc:coordinates_IfcCartesianPoint ?lenMeasurementList_x . \n" +
                "        ?lenMeasurementList_x list:hasContents ?x . \n" +
                "        ?x express:hasDouble ?x_Value . \n" +
                "        ?lenMeasurementList_x list:hasNext ?y_List . \n" +
                "        ?y_List list:hasContents ?y . \n" +
                "        ?y express:hasDouble ?y_Value . \n" +
                "    } ORDER BY asc(?cartesianPoint) \n" +
                "}\n" +
                "GROUP BY ?name ?space";
        return QueryExecutionFactory
                .create(query, this.dataset)
                .execSelect();
    }

    /**
     * in order to display the rooms correctly, we need to convert the string to an array of arrays and add the first item
     * if we don't do this, the graphical representation would not be correct -> a line (representing the wall) would be missing
     *
     * @param input the geometry
     * @return an array of an array with the geometry
     */
    private List<List<Double>> convertToCoordinates(String input) {
        List<List<Double>> coordinates = new ArrayList<>();
        Pattern pattern = Pattern.compile("[0-9]{1,13}(.[0-9]*)?");
        List<Double> res = new ArrayList<>();
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            res.add(Double.valueOf(matcher.group(0)));
        }
        for (int i = 0; i < res.size(); i = i + 2) {
            coordinates.add(Arrays.asList(res.get(i), res.get(i + 1)));
        }
        coordinates.add(coordinates.get(0));
        return coordinates;
    }

    public ResultSet getSensorsWithinRoom(String roomUri) {
        return QueryExecutionFactory
                .create("PREFIX ifc: <http://standards.buildingsmart.org/IFC/DEV/IFC2x3/TC1/OWL#>\n" +
                        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                        "PREFIX express: <https://w3id.org/express#>\n" +
                        "Select DISTINCT ?proxy_name ?proxy where {\n" +
                        "    ?spatial_Structure ifc:relatedElements_IfcRelContainedInSpatialStructure  ?proxy .\n" +
                        "    ?spatial_Structure ifc:relatingStructure_IfcRelContainedInSpatialStructure  <" + roomUri + "> .\n" +
                        "    ?spatial_Structure rdf:type  ifc:IfcRelContainedInSpatialStructure .\n" +
                        "    ?proxy ifc:objectType_IfcObject ?proxy_type .\n" +
                        "    ?proxy ifc:name_IfcRoot ?proxy_label .\n" +
                        "    ?proxy_label express:hasString ?proxy_name .\n" +
                        "    filter contains(?proxy_name,\"sensor\") } ", this.dataset)
                .execSelect();
    }

    public ResultSet getSensorsWithinBuildingElements(String roomUri) {
        String query = "" +
                "PREFIX ifc: <http://standards.buildingsmart.org/IFC/DEV/IFC2x3/TC1/OWL#>\n" +
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "\n" +
                "PREFIX express: <https://w3id.org/express#>\n" +
                "SELECT DISTINCT ?proxy ?name WHERE {\n" +
                "\t?space rdf:type  ifc:IfcSpace .\n" +
                "    ?space ifc:name_IfcRoot ?label .\n" +
                "    ?rel rdf:type  ifc:IfcRelSpaceBoundary .\n" +
                "    ?rel ifc:relatingSpace_IfcRelSpaceBoundary ?space .\n" +
                "    ?space ifc:longName_IfcSpatialStructureElement <" + roomUri + "> .\n" +
                "    ?rel ifc:relatedBuildingElement_IfcRelSpaceBoundary ?buildingElement .\n" +
                "    ?buildingElement  rdf:type ifc:IfcWallStandardCase .  \n" +
                "    ?el rdf:type ifc:IfcRelVoidsElement .\n" +
                "    ?el ifc:relatingBuildingElement_IfcRelVoidsElement ?buildingElement .\n" +
                "    ?el ifc:relatedOpeningElement_IfcRelVoidsElement ?proxy .\n" +
                "    ?space ifc:longName_IfcSpatialStructureElement ?uri . \n" +
                "    ?uri express:hasString ?name . \n" +
                "}\n";
        return QueryExecutionFactory
                .create("" +
                        "PREFIX ifc: <http://standards.buildingsmart.org/IFC/DEV/IFC2x3/TC1/OWL#>\n" +
                        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                        "\n" +
                        "PREFIX express: <https://w3id.org/express#>\n" +
                        "SELECT DISTINCT ?proxy ?name WHERE {\n" +
                        "\t?space rdf:type  ifc:IfcSpace .\n" +
                        "    ?space ifc:name_IfcRoot ?label .\n" +
                        "    ?rel rdf:type  ifc:IfcRelSpaceBoundary .\n" +
                        "    ?rel ifc:relatingSpace_IfcRelSpaceBoundary <" + roomUri + "> .\n" +
                        "    ?rel ifc:relatedBuildingElement_IfcRelSpaceBoundary ?buildingElement .\n" +
                        "    ?buildingElement  rdf:type ifc:IfcWallStandardCase .  \n" +
                        "    ?el rdf:type ifc:IfcRelVoidsElement .\n" +
                        "    ?el ifc:relatingBuildingElement_IfcRelVoidsElement ?buildingElement .\n" +
                        "    ?el ifc:relatedOpeningElement_IfcRelVoidsElement ?proxy .\n" +
                        "}\n", this.dataset)
                .execSelect();
    }

    /**
     * save the information of the sensors to the database. As the sensor family is a custom family, we need to extract
     * the information with switch case as the label indicates what the value is used for
     *
     * @param newSensor         the sensor with some basic information that we created in the previous method
     * @param sensorInformation the resultset of the query containing more information about the sensor
     */
    private void setSensorInformation(Sensor newSensor, ResultSet sensorInformation) {
        while (sensorInformation.hasNext()) {
            QuerySolution q = sensorInformation.next();
            String label = q.get("id_label").toString();
            try {
                switch (label) {
                    case "Sensor ID":
                        newSensor.setInfluxIdentifier(q.get("stringValue").toString().toCharArray());
                        newSensor.setName(q.get("stringValue").toString());
                        break;
                    case "Abtastrate":
                        newSensor.setSamplingRate(q.get("doubleValue").asLiteral().getDouble());
                        break;
                    case "Messbereich_Obergrenze":
                        newSensor.setMaxValue(q.get("doubleValue").asLiteral().getDouble());
                        break;
                    case "Messbereich_Untergrenze":
                        newSensor.setMinValue(q.get("doubleValue").asLiteral().getDouble());
                        break;
                    case "Datenuebertragung drahtlos":
                        if (q.get("booleanValue").asLiteral().getString().equals("true")) {
                            newSensor.setTransmissionType(TransmissionType.RDIF);
                        } else if (q.get("booleanValue").asLiteral().getString().equals("false")) {
                            newSensor.setTransmissionType(TransmissionType.CABLE_BOUND);
                        }
                        break;
                    case "Typ":
                    case "Type":
                        if (q.get("stringValue").asLiteral().getString().contains("Multi")) {
                            //set to multi
                            newSensor.setSensorType(SensorType.MULTI);
                        } else if (q.get("stringValue").asLiteral().getString().contains("Temp")) {
                            newSensor.setSensorType(SensorType.TEMPERATURE);
                        } else if (q.get("stringValue").asLiteral().getString().contains("Feucht")) {
                            newSensor.setSensorType(SensorType.HUMIDITY);
                        } else if (q.get("stringValue").asLiteral().getString().contains("Licht")) {
                            newSensor.setSensorType(SensorType.LIGHT);
                        } else if (q.get("stringValue").asLiteral().getString().contains("Druck")) {
                            newSensor.setSensorType(SensorType.PRESSURE);
                        }
                        break;
                    case "Bucket Name":
                        newSensor.setBucketName(q.get("stringValue").toString());
                        break;
                    default:
                        break;
                }
            } catch (NullPointerException np) {
                // sensor does contain null literals

            }
        }

    }

    public ResultSet getElementInformation(String elementUri) {
        return QueryExecutionFactory
                .create("PREFIX ifc: <http://standards.buildingsmart.org/IFC/DEV/IFC2x3/TC1/OWL#>\n" +
                        "PREFIX express: <https://w3id.org/express#>\n" +
                        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                        "select ?id_label ?doubleValue ?stringValue ?booleanValue where { \n" +
                        "    ?props rdf:type  ifc:IfcRelDefinesByProperties .\n" +
                        "    ?props ifc:relatedObjects_IfcRelDefines <" + elementUri + ">.\n" +
                        "\t<" + elementUri + "> ifc:name_IfcRoot ?proxy_label .\n" +
                        "    ?proxy_label express:hasString ?proxy_name .\n" +
                        "    ?props ifc:relatingPropertyDefinition_IfcRelDefinesByProperties ?properties .\n" +
                        "    ?properties ifc:hasProperties_IfcPropertySet ?property_set .\n" +
                        "    ?properties ifc:name_IfcRoot ?label_root .\n" +
                        "    ?label_root express:hasString ?label_root_name .\n" +
                        "    ?property_set ifc:nominalValue_IfcPropertySingleValue ?single_value .\n" +
                        "    ?property_set ifc:name_IfcProperty ?identifier .\n" +
                        "    ?identifier express:hasString ?id_label .\n" +
                        "    OPTIONAL {?single_value express:hasDouble ?doubleValue} .\n" +
                        "    OPTIONAL {?single_value express:hasString ?stringValue} .\n" +
                        "    OPTIONAL {?single_value express:hasBoolean ?booleanValue} .\n" +
                        "} \n", this.dataset)
                .execSelect();
    }

    private ResultSet getSwitchingDevice(String roomUri) {
        return QueryExecutionFactory
                .create("PREFIX ifc: <http://standards.buildingsmart.org/IFC/DEV/IFC2x3/TC1/OWL#>\n" +
                        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                        "PREFIX express: <https://w3id.org/express#>\n" +
                        "Select DISTINCT *  where {\n" +
                        "    ?proxy ifc:objectType_IfcObject ?proxy_type .\n" +
                        "    ?spatialStructure ifc:relatedElements_IfcRelContainedInSpatialStructure ?proxy .\n" +
                        "    ?spatialStructure ifc:relatingStructure_IfcRelContainedInSpatialStructure <" + roomUri + "> .\n" +
                        "    ?proxy_type express:hasString ?proxy_type_name .\n" +
                        "    ?proxy ifc:name_IfcRoot ?proxy_label .\n" +
                        "    ?proxy_label express:hasString ?proxy_name .\n" +
                        "    filter contains(?proxy_name,\"Steuer\") \n" +
                        "}", this.dataset)
                .execSelect();
    }

    private ResultSet getRelatedElementsOfSpace(String uri){
        return QueryExecutionFactory.create("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
                "PREFIX ifcowl: <http://ifcowl.openbimstandards.org/IFC2X3_TC1#> \n" +
                "PREFIX express: <https://w3id.org/express#> \n" +
                "PREFIX list: <https://w3id.org/list#> \n" +
                "PREFIX ifc: <http://standards.buildingsmart.org/IFC/DEV/IFC2x3/TC1/OWL#>\n" +
                "SELECT ?relatedSpace where { \n" +
                "    ?spaceBoundary rdf:type ifc:IfcRelSpaceBoundary . \n" +
                "    ?spaceBoundary ifc:relatingSpace_IfcRelSpaceBoundary ?space . \n" +
                "    ?space ifc:longName_IfcSpatialStructureElement <" + uri + "> .\n" +
                "    ?agr rdf:type ifc:IfcRelAggregates . \n" +
                "    ?agr ifc:relatingObject_IfcRelDecomposes ?storey . \n" +
                "    ?agr ifc:relatedObjects_IfcRelDecomposes ?space . \n" +
                "    ?spaceBoundary ifc:relatedBuildingElement_IfcRelSpaceBoundary ?relatedSpace .\n" +
                "    ?relatedSpace rdf:type ?type .\n" +
                "FILTER (regex(str(?type), \"Wall\") || regex(str(?type), \"Slab\") || regex(str(?type), \"Roof\"))" +
                "}\n",
                dataset).execSelect();
    }

    private void setSwitchingDeviceInformation(SwitchingDevice device, ResultSet deviceInformation) {
        while (deviceInformation.hasNext()) {
            QuerySolution q = deviceInformation.next();
            String label = q.get("id_label").toString();
            try {
                switch (label) {
                    case "Name":
                        device.setName((q.get("stringValue").toString()));
                        break;
                    case "Slug":
                        device.setSlug((q.get("stringValue").toString()));
                        break;
                    case "Statuspfad":
                        device.setStatusPath((q.get("stringValue").toString()));
                        break;
                    case "Einschaltpfad":
                        device.setOnPath((q.get("stringValue").toString()));
                        break;
                    case "Ausschaltpfad":
                        device.setOffPath((q.get("stringValue").toString()));
                        break;
                    case "IP":
                        device.setIp((q.get("stringValue").toString()));
                        break;
                    case "Token":
                        device.setToken((q.get("stringValue").toString()));
                        break;
                    case "Automatische Schaltung":
                        device.setAutomatic(q.get("booleanValue").asLiteral().getBoolean());
                    case "Beschreibung des Relais":
                        device.setComment(q.get("stringValue").toString());
                    default:
                        break;
                }
            } catch (NullPointerException np) {
                // sensor contains null literals

            }
        }
    }
    private ResultSet getBuildingElementProxyInformation(String elementUri) {
        return QueryExecutionFactory
                .create("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                        "PREFIX ifc: <http://standards.buildingsmart.org/IFC/DEV/IFC2x3/TC1/OWL#>\n" +
                        "PREFIX express: <https://w3id.org/express#>\n" +
                        "SELECT DISTINCT ?id_label ?stringValue  ?booleanValue WHERE {\n" +
                        "    ?proxy_defines rdf:type  ifc:IfcRelDefinesByProperties .\n" +
                        "    ?proxy_defines ifc:relatedObjects_IfcRelDefines <" + elementUri + "> .\n" +
                        "    <" + elementUri + "> rdf:type  ifc:IfcBuildingElementProxy .\n" +
                        "    <" + elementUri + "> ifc:name_IfcRoot ?proxy_label .\n" +
                        "    ?proxy_label express:hasString ?proxy_name .\n" +
                        "    ?proxy_defines ifc:relatingPropertyDefinition_IfcRelDefinesByProperties ?properties .\n" +
                        "    ?properties ifc:hasProperties_IfcPropertySet ?property_set .\n" +
                        "    ?properties ifc:name_IfcRoot ?label_root .\n" +
                        "    ?label_root express:hasString ?label_root_name .\n" +
                        "    ?property_set ifc:nominalValue_IfcPropertySingleValue ?single_value .\n" +
                        "    ?property_set ifc:name_IfcProperty ?identifier .\n" +
                        "    ?identifier express:hasString ?id_label .\n" +
                        "    OPTIONAL {?single_value express:hasString ?stringValue} .\n" +
                        "    OPTIONAL {?single_value express:hasBoolean ?booleanValue} .\n" +
                        "}\n", this.dataset)
                .execSelect();
    }

    private ResultSet getParameterValues(String uri){
        return QueryExecutionFactory.create("" +
                        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                        "PREFIX ifc: <http://standards.buildingsmart.org/IFC/DEV/IFC2x3/TC1/OWL#>\n" +
                        "PREFIX express: <https://w3id.org/express#>\n" +
                        "SELECT DISTINCT ?typeParameterLabel ?stringValue ?doubleValue ?booleanValue WHERE \n" +
                        "{\n" +
                        "    {\n" +
                        "        ?relatedType ifc:relatedObjects_IfcRelDefines <" + uri + "> .\n" +
                        "        ?definesProperties ifc:relatedObjects_IfcRelDefines <" + uri + "> .\n" +
                        "        ?definesProperties ifc:relatingPropertyDefinition_IfcRelDefinesByProperties ?propertySet .\n" +
                        "        ?propertySet ifc:hasProperties_IfcPropertySet ?properties .\n" +
                        "        ?properties ifc:name_IfcProperty ?label_root .\n" +
                        "        ?label_root express:hasString ?typeParameterLabel .\n" +
                        "        ?properties ifc:nominalValue_IfcPropertySingleValue ?single_value .\n" +
                        "        OPTIONAL {?single_value express:hasString ?stringValue} .\n" +
                        "        OPTIONAL {?single_value express:hasBoolean ?booleanValue} .\n" +
                        "        OPTIONAL {?single_value express:hasDouble ?doubleValue} .\n" +
                        "    } UNION {\n" +
                        "        ?relatedType ifc:relatedObjects_IfcRelDefines <" + uri + "> .\n" +
                        "        ?relatedType ifc:relatingType_IfcRelDefinesByType ?type .\n" +
                        "        ?type ifc:hasPropertySets_IfcTypeObject ?TypePropertySet .\n" +
                        "        ?type ifc:name_IfcRoot ?proxy_label .\n" +
                        "        ?proxy_label express:hasString ?proxy_name .\n" +
                        "        ?TypePropertySet ifc:hasProperties_IfcPropertySet ?properties .\n" +
                        "        ?properties ifc:name_IfcProperty ?label_root .\n" +
                        "        ?label_root express:hasString ?typeParameterLabel .\n" +
                        "        ?properties ifc:nominalValue_IfcPropertySingleValue ?single_value .\n" +
                        "        OPTIONAL {?single_value express:hasString ?stringValue} .\n" +
                        "        OPTIONAL {?single_value express:hasBoolean ?booleanValue} .\n" +
                        "        OPTIONAL {?single_value express:hasDouble ?doubleValue} .\n" +
                        "    }\n" +
                        "}"
                , dataset).execSelect();
    }
    private void setBuildingElementInformation(BuildingElement buildingElement, ResultSet elementInformation) {
        while (elementInformation.hasNext()) {
            QuerySolution q = elementInformation.next();
            String label = q.get("typeParameterLabel").toString();
            try {
                switch (label) {
                    case "Area":
                    case "Fläche":
                        buildingElement.setSurface((q.get("doubleValue").asLiteral().getDouble()));
                        break;
                    case "Thermal Resistance (R)":
                        case "Thermischer Widerstand (R)":
                        buildingElement.setHeatCapacity((q.get("doubleValue").asLiteral().getDouble()));
                        break;
                    case "Heat Transfer Coefficient (U)":
                        case "Wärmedurchgangskoeffizient (U)":
                        buildingElement.setConductance((q.get("doubleValue").asLiteral().getDouble()));
                        break;
                    //case "Thermisch Wirksame Masse":
                    //    buildingElement.setConductance((q.get("doubleValue").asLiteral().getDouble()));
                    //    break;
                    case "Structural Material":
                        case "Tragendes Material":
                        // calculate density
                        buildingElement.setDensityPerCMInGrams(buildingElementService.getDensityFromMaterial(q.get("stringValue").toString()));
                        break;
                    case "Family Name":
                    case "Familienname":
                        // calculate density
                        if(Double.isNaN(buildingElement.getDensityPerCMInGrams())){
                            buildingElement.setDensityPerCMInGrams(buildingElementService.getDensityFromMaterial(q.get("stringValue").toString()));
                        }
                        break;
                    default:
                        break;
                }
            } catch (NullPointerException np) {
                // sensor does contain null literals

            }
        }
    }
}
