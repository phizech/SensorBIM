import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ViewService} from './view.service';
import {ActivatedRoute, Router} from '@angular/router';
import {Room} from '../../entities/room';
import {Level} from '../../entities/level';
import {SensorService} from '../sensor/sensor.service';

@Component({
  selector: 'app-view',
  templateUrl: './view.component.html',
  styleUrls: ['./view.component.css', '../CssTemplates/tables.css']
})
export class ViewComponent implements OnInit {

  public data;
  public selectedSpaces;
  public rooms: Room[];
  public allSensorTypes = ['Temperature', 'Humidity', 'Matrial_Humidity'];
  public selectedSensorType: String = this.allSensorTypes[0];
  public levels;
  public selectedLevel: Level;
  public buildingId;

  constructor(
    private http: HttpClient,
    private _s: ViewService,
    private sensorService: SensorService,
    private route: ActivatedRoute,
    private router: Router
  ) {
  }

  ngOnInit() {
    this.route.paramMap
      .subscribe(params => {
          this.buildingId = +params.get('buildingId');
        }
      );
    this.getLevels();
  }

  getLevels() {
    this._s.getLevels(this.buildingId).subscribe(res => {
      this.selectedLevel = res[0];
      this.onChange();
      this.levels = res;
    }, err => console.log(err));
  }

  async onChange() {
    this._s.getRoomsForLevel(this.selectedLevel.id).subscribe(async res => {
      //    setInterval(async () => {         // replaced function() by ()=>
      this.rooms = res;
      this.data = await this._s._roomsToGeoJSON(res, this.selectedSensorType);
      //  }, 1000); // 1000
    }, err => console.log(err));
  }

  containsSensor(name) {
    const r: Room = this.rooms.find(room => room.name === name);
    return (r.sensors.length !== 0);
  }

  roomClick(ev) {
    this.selectedSpaces = [ev.uri];
    if (this.containsSensor(ev.uri)) {
      this.router.navigate(['/', this.buildingId, 'sensor', this.selectedLevel.id, ev.uri]);
    }
  }

  canvasClick() {
    this.selectedSpaces = [];
  }

  switchType(btn: string) {
    this.selectedSensorType = btn;
    this.onChange();
  }
}
