import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-reception',
  templateUrl: './reception.component.html',
  styleUrls: ['./reception.component.css']
})
export class ReceptionComponent implements OnInit {

  public roomNo;

  constructor(private router: Router, private http: HttpClient) { }

  ngOnInit() {
  }

  getRoom() {
    this.http.post(environment.baseApi + '/rooms', null, {responseType: 'text'}).subscribe((roomId: string) => {
      if (!roomId) {
        alert('No Room Available...');
      } else {
        this.router.navigate(['room', roomId]);
      }
    }, (err) => {
      console.error(err);
    });
  }

  joinRoom() {
    this.router.navigate(['/room', this.roomNo]);
  }

}
