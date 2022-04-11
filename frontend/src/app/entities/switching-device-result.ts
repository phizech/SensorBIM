import {SwitchingDevice} from './switchingDevice';

export interface SwitchingDeviceResult {

  device: SwitchingDevice;
  active: boolean;
  lastChanged: string;
  latestMeanMeasurement: number;
  automatic: boolean;
}
