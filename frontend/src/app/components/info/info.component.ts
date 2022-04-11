import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {AppComponent} from '../../app.component';

@Component({
  selector: 'app-info',
  templateUrl: './info.component.html',
  styleUrls: ['./info.component.css', '../CssTemplates/configuration.css']
})
export class InfoComponent implements OnInit {

  ids: Array<String> = ['one', 'two', 'three', 'four'];

  constructor(private router: Router,
              private route: ActivatedRoute,
              public appComponent: AppComponent) {
  }

  ngOnInit() {
    this.route.fragment.subscribe(f => {
      const element = document.querySelector('#' + f);
      if (element) {
        element.scrollIntoView();
      }
    });
  }

}
