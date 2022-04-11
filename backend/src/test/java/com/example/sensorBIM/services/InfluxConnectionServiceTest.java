package com.example.sensorBIM.services;

import com.example.sensorBIM.services.InfluxDB.InfluxConnectionService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InfluxConnectionServiceTest {

    @Autowired
    private InfluxConnectionService influxConnectionService;

    @Test
    public void testInfluxConnectionValidURL() {
        Assert.assertTrue(influxConnectionService.testInfluxConnection("http://qe-sensorbim.uibk.ac.at:8086/"));
    }

    @Test
    public void testInfluxConnectionInvalidURL() {
        Assert.assertFalse(influxConnectionService.testInfluxConnection("someURL"));
    }

}