package com.example.sensorBIM.services.Configuration;

import com.example.sensorBIM.HttpBody.Response;
import com.example.sensorBIM.HttpBody.ResponseStatus;
import com.example.sensorBIM.model.Building;
import com.example.sensorBIM.services.Building.BuildingService;
import com.example.sensorBIM.services.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;

/**
 * this class handles the upload
 */
@RestController
@Scope("application")
@RequestMapping("/config")
public class UploadService {

    @Autowired
    private Ifc2IcfOWLConverterService ifc2IcfOWLConverterService;

    @Autowired
    private QueryService queryService;

    @Autowired
    private BuildingService buildingService;

    /**
     * this saves the content to a file in directory
     *
     * @param content the content of the uploaded file
     * @return returns whether it was a success or not. if not, the response contains an error message
     */
    @PostMapping("/upload")
    public ResponseEntity<Response<File>> uploadFile(@RequestBody String content) throws IOException {
        File uploadedFile = new File(Constants.UPLOAD_FILE_DIRECTORY);
        File outputFile = new File(Constants.OUTPUT_TTL);
        if (uploadedFile.createNewFile()) {
            System.out.println("Files created: " + uploadedFile.getName());
        }
        if (outputFile.createNewFile()) {
            System.out.println("Files created: " + outputFile.getName());
        }
        PrintWriter out = new PrintWriter(Constants.UPLOAD_FILE_DIRECTORY);
        out.println(content);
        out.close();
        if (uploadedFile.exists() && outputFile.exists()) {
            return new ResponseEntity<>(new Response<>(ResponseStatus.SUCCESS, "upload.file.successful", uploadedFile), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Response<>(ResponseStatus.FAILURE, "upload.file.failed", uploadedFile), HttpStatus.OK);
    }

    /**
     * converts the uploaded ifc file to a turtle file, saves data to mysql and deletes the uploaded and converted file
     *
     * @param building the building to convert, only contains the name
     * @return the response: either success or no success
     */
    @PostMapping(value = "/convert")
    public ResponseEntity<Response<Object>> convertIFC(@RequestBody Building building) throws IOException {
        ResponseEntity<Response<Object>> response = convertAndUploadToMySQL(building, Constants.UPLOAD_FILE_DIRECTORY, Constants.OUTPUT_TTL);
        if (Objects.requireNonNull(response.getBody()).getResponseStatus() != ResponseStatus.FAILURE) {
            return response;
        }
        new File(Constants.UPLOAD_FILE_DIRECTORY).delete();
        //new File(Constants.OUTPUT_TTL).delete();
        return response;
    }

    /**
     * @param building the building to convert and save to mysql
     * @param upload   the path to the uploaded file
     * @param output   the path to the output file
     * @return return whether it was a success or fail. if it is a fail, an error message is returned
     */
    public ResponseEntity<Response<Object>> convertAndUploadToMySQL(@RequestBody Building building, String upload, String output) throws IOException {
        Response<Object> response;
        String saveBuildingMessage = buildingService.isSavingBuildingAllowed(building);
        if (saveBuildingMessage != null) {
            return new ResponseEntity<>(new Response<>(ResponseStatus.WARNING, saveBuildingMessage, null), HttpStatus.OK);
        } else {
            if (!ifc2IcfOWLConverterService.convertIFCtoIFCOWL(upload, output)) {
                response = new Response<>(ResponseStatus.FAILURE, "upload.file.convertingFailed", null);
            } else {
                response = queryService.loadIFCOWL2Database(building, output);
            }
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
