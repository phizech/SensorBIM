package com.example.sensorBIM.services;

import com.example.sensorBIM.HttpBody.SwitchingDeviceResult;
import com.example.sensorBIM.model.SwitchingDevice;
import com.example.sensorBIM.services.Sensor.SwitchingDeviceService;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@WebAppConfiguration
@RunWith(MockitoJUnitRunner.class)
public class SwitchingDeviceServiceTest {

    @Spy
    @InjectMocks
    private SwitchingDeviceService switchingDeviceService;

    @Spy
    @InjectMocks
    private RestService restService;

    @Before
    public void setup() throws JSONException {
        switchingDeviceService = mock(SwitchingDeviceService.class);
        restService = mock(RestService.class);
        String result = "{\"active\": true, " +
                "\"automatic_temperature_control\": true, " +
                "\"last_change\": \"2022-03-17T07:50:21.839Z\", " +
                "\"mean_5_min_temperature\": 1.294137695652174}\n";
        when(this.restService.getRequest("some url", "some token")).thenReturn(new JSONObject(result));
    }

    @Test
    public void testLoadSwitchingDevice() throws JSONException, IOException {
        SwitchingDevice device = getSwitchingDevice("Device 1");
        Assert.assertNotNull(device);
        SwitchingDeviceResult result = switchingDeviceService.getStatusOfSwitchingDevice(device);
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetStatus() throws JSONException, IOException {
        SwitchingDevice device = getSwitchingDevice("Device 2");
        SwitchingDeviceResult status = switchingDeviceService.getStatusOfSwitchingDevice(device);
        Assert.assertNotNull(status);
        JSONObject result = restService.getRequest("some url", "some token");
        Assert.assertNotNull(result);
    }

    public SwitchingDevice getSwitchingDevice() {
        return switchingDeviceService.findSwitchingDevicesByBuildingId(1L).stream().findAny().get().getDevice();
    }

    public SwitchingDevice getSwitchingDevice(String name) {
        SwitchingDevice device = new SwitchingDevice();
        device.setAutomatic(true);
        device.setName(name);
        device.setIp("some url");
        device.setOffPath("/setOff");
        device.setOnPath(("/setOn"));
        device.setSlug("some slug");
        device.setToken("some token");
        device.setStatusPath("");
        return device;
    }
}
