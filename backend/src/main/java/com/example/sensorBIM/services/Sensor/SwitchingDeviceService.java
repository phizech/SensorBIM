package com.example.sensorBIM.services.Sensor;

import com.example.sensorBIM.HttpBody.Response;
import com.example.sensorBIM.HttpBody.ResponseStatus;
import com.example.sensorBIM.HttpBody.SwitchingDeviceResult;
import com.example.sensorBIM.model.Enums.SwitchingDeviceStatus;
import com.example.sensorBIM.model.SwitchingDevice;
import com.example.sensorBIM.repository.SwitchingDeviceRepository;
import com.example.sensorBIM.services.RestService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

@Component
@Scope("application")
@Transactional
public class SwitchingDeviceService {

    @Autowired
    private SwitchingDeviceRepository switchingDeviceRepository;

    @Autowired
    private RestService restService;

    public SwitchingDevice findSwitchingDeviceById(Long deviceId) {
        return switchingDeviceRepository.findById(deviceId).orElse(null);
    }

    /**
     * get all the switching devices contained in a building and get the status of them
     *
     * @param buildingId the id of the building of which we want to get the data
     * @return a collection of all the switching devices contained in the building
     */
    public Collection<SwitchingDeviceResult> findSwitchingDevicesByBuildingId(Long buildingId) {
        Collection<SwitchingDevice> devices = switchingDeviceRepository.findAllSwitchingDevicesForBuilding(buildingId);
        Collection<SwitchingDeviceResult> results = new ArrayList<>();
        if (!devices.isEmpty()) {
            devices.forEach(device -> {
                try {
                    results.add(getStatusOfSwitchingDevice(device));
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            });
            new Response<Collection<SwitchingDevice>>(ResponseStatus.SUCCESS, "Loaded switching devices successfully!", null);
        } else {
            Response<Collection<SwitchingDevice>> res = new Response<Collection<SwitchingDevice>>(ResponseStatus.FAILURE, "The building does not contain any devices!", null);
        }
        return results;
    }

    /**
     * only saves the switching devices if it is valid. in this case we check if the path is unique
     * and all the properties of the switching device are valid: on/off and status path should contain a value
     * @param switchingDevice the switching device we want to save
     * @return true, if saving the device is allowed
     */
    public boolean isSavingSwitchingDeviceAllowed(SwitchingDevice switchingDevice) {
        return switchingDevicePathsUnique(switchingDevice) && hasValidProperties(switchingDevice);
    }

    private boolean hasValidProperties(SwitchingDevice switchingDevice) {
        return !Objects.equals(switchingDevice.getIp(), "") &&
                !Objects.equals(switchingDevice.getOffPath(), "") &&
                !Objects.equals(switchingDevice.getOnPath(), "") &&
                !Objects.equals(switchingDevice.getStatusPath(), "");
    }

    /**
     * only if the status path, the off path and the on path (including ip and slug) are unique,
     * it is allowed to save the device
     *
     * @param switchingDevice the device we want to save
     * @return true if the paths are unique
     */
    public boolean switchingDevicePathsUnique(SwitchingDevice switchingDevice) {
        return switchingDeviceRepository.findSwitchingDeviceByIPAndOffPath(switchingDevice.getIp(), switchingDevice.getSlug(), switchingDevice.getOffPath()).isEmpty() &&
                switchingDeviceRepository.findSwitchingDeviceByIPAndOnPath(switchingDevice.getIp(), switchingDevice.getSlug(), switchingDevice.getOnPath()).isEmpty() &&
                switchingDeviceRepository.findSwitchingDeviceByIPAndStatusPath(switchingDevice.getIp(), switchingDevice.getSlug(), switchingDevice.getStatusPath()).isEmpty();
    }

    /**
     * send a request to the path of the device in order to get the status
     *
     * @param device the device of which we want to get the status
     * @return the status of the given device
     */
    public SwitchingDeviceResult getStatusOfSwitchingDevice(SwitchingDevice device) throws IOException, JSONException {
        JSONObject json = restService.getRequest(getPath(device) + device.getStatusPath(), device.getToken());
        if (json != null) {
            try {
                if (json.getBoolean("active")) {
                    device.setStatus(SwitchingDeviceStatus.ON);
                } else {
                    device.setStatus(SwitchingDeviceStatus.OFF);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                device.setStatus(SwitchingDeviceStatus.UNKNOWN);
            }

            return jsonToSwitchingDevice(json, device);
        }
        device.setStatus(SwitchingDeviceStatus.UNKNOWN);
        SwitchingDeviceResult result = new SwitchingDeviceResult();
        result.setDevice(device);
        return result;
    }

    /**
     * change the status of the switching device. if it is off, we turn it on. if it is on or unknown, we turn it off
     *
     * @param device the device of which we want to change the status
     * @return the switching device with the changed status
     */
    public SwitchingDeviceResult setSwitchingDeviceStatus(SwitchingDevice device) throws JSONException {
        JSONObject res;
        String path = getPath(device);
        if (device.getStatus() == SwitchingDeviceStatus.OFF) {
            res = restService.getRequest(path + device.getOnPath(), device.getToken());
        } else {
            res = restService.getRequest(path + device.getOffPath(), device.getToken());
        }
        return jsonToSwitchingDevice(res, device);
    }

    /**
     * get the path of the switching device.
     * if ip is 127.0.0.0 and the slug "slug", we need to add a "/" -> 127.0.0.0/slug instead of 127.0.0.0slug
     *
     * @param device of which we want to get the status
     * @return the valid path of the device
     */
    private String getPath(SwitchingDevice device) {
        String path;
        if (!device.getSlug().isEmpty()) {
            if (device.getIp().endsWith("/") || device.getSlug().startsWith("/")) {
                path = device.getIp() + device.getSlug();
            } else {
                path = device.getIp() + "/" + device.getSlug();
            }
        } else {
            path = device.getIp() + device.getSlug();
        }
        return path;
    }

    /**
     * save the data from the returned json to a switching device result
     *
     * @param json   the json which was returned from the request
     * @param device the device of which we got the json
     * @return the result, containing the device, boolean for active, at what time it was changed and latest measurement
     */
    private SwitchingDeviceResult jsonToSwitchingDevice(JSONObject json, SwitchingDevice device) throws JSONException {
        SwitchingDeviceResult result = new SwitchingDeviceResult();
        result.setDevice(device);
        if (json != null) {
            Iterator keys = json.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                if (key.equals("active")) {
                    result.setActive(json.getBoolean(key));
                } else if (key.equals("last_change")) {
                    result.setLastChanged(json.getString(key));
                } else if (key.contains("mean")) {
                    result.setMeanMeasurementOf5Minutes(json.getDouble(key));
                } else if (key.contains("automatic")) {
                    result.setAutomatic(json.getBoolean(key));
                }
            }
        }
        return result;
    }

    /**
     * switch the mode of the switching device: either manual or automatic
     *
     * @param device    of which we want to change the mode
     * @param automatic true, if automatic, false if manual
     * @return the result, containing the device, boolean for active, at what time it was changed and latest measurement
     */
    public SwitchingDeviceResult switchMode(SwitchingDevice device, boolean automatic) throws IOException, InterruptedException, JSONException {
        String payload = "{\"enable_automatic\": " + automatic + "}"; // maybe we will need the value of automatic
        HttpResponse<String> result = restService.postRequest("http://" + getPath(device) + "setAutomatic/", device.getToken(), payload);
        SwitchingDeviceResult res = new SwitchingDeviceResult();
        if (result.statusCode() == 200) {
            device.setAutomatic(automatic);
            switchingDeviceRepository.save(device);
        }
        res.setDevice(device);
        return res;
    }

    public void deleteDevice(SwitchingDevice switchingDeviceResult) {
        switchingDeviceRepository.delete(switchingDeviceResult);
    }
}
