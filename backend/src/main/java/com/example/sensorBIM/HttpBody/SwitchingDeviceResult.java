package com.example.sensorBIM.HttpBody;

import com.example.sensorBIM.model.SwitchingDevice;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SwitchingDeviceResult {

    SwitchingDevice device;
    boolean active;
    String lastChanged;
    Double meanMeasurementOf5Minutes;
    boolean automatic;
}
