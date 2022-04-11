package com.example.sensorBIM.services;

import com.example.sensorBIM.HttpBody.Response;
import com.example.sensorBIM.HttpBody.ResponseStatus;
import com.example.sensorBIM.TestConstants;
import com.example.sensorBIM.model.Building;
import com.example.sensorBIM.model.User;
import com.example.sensorBIM.services.Configuration.UploadService;
import com.example.sensorBIM.services.User.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class UploadServiceTest {

    @Autowired
    private UploadService uploadService;

    @Autowired
    private UserService userService;

    @Test
    public void testUploadFile() throws IOException {
        Path path = Path.of(TestConstants.UPLOAD_FILE_DIRECTORY);
        String content = Files.readString(path, StandardCharsets.US_ASCII);
        Assert.assertFalse(content.isEmpty());
        Response<File> response = uploadService.uploadFile(content).getBody();
        Assert.assertNotNull(response);
        Assert.assertEquals(ResponseStatus.SUCCESS, response.getResponseStatus());
        Assert.assertEquals("upload.file.successful", response.getMessage());
    }

    @Test
    public void testConvertAndUpload() throws IOException {
        User user = userService.findUserByUsername("steverogers");
        Building building = new Building();
        building.setUser(user);
        building.setInfluxDatabaseUrl(TestConstants.INFLUX_URL);
        building.setOrganizationName(TestConstants.ORGANIZATION_NAME);
        building.setInfluxDBToken(TestConstants.INFLUX_TOKEN);
        building.setName("UploadedIFCBuilding");
        Response<Object> response = uploadService.convertAndUploadToMySQL(building, TestConstants.UPLOAD_FILE_DIRECTORY, TestConstants.OUTPUT_TTL).getBody();
        Assert.assertNotNull(response);
        Assert.assertEquals(ResponseStatus.SUCCESS, response.getResponseStatus());
    }
}
