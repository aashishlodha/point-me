import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { RoomService } from '../shared/services/room.service';
import { environment } from 'src/environments/environment.prod';

@Component({
  selector: 'app-rooms',
  templateUrl: './rooms.component.html',
  styleUrls: ['./rooms.component.css']
})
export class RoomsComponent implements OnInit, OnDestroy {
  public rooms: any[] = [];
  private roomSub;
  public roomNo;

  constructor(private http: HttpClient, private router: Router) {}

  public ngOnInit() {
    this.getRoomsData();
    this.roomSub = setInterval(() => {
      this.getRoomsData();
    }, 4000);
  }

  getRoomsData() {
    this.http.get(environment.baseApi + 'rooms').subscribe((data) => {
      this.rooms = data as [];
    });
  }

  joinRoom() {
    if (this.roomNo && +this.roomNo > 0 && +this.roomNo <= this.rooms.length) {
      this.router.navigate(['/room', this.roomNo]);
    } else {
      alert('Wrong room number, please try valid (1 to ' + this.rooms.length + ')');
    }
  }

  enterRoom(roomNo) {
    this.router.navigate(['/room', roomNo]);
  }

  roomTrackByFunction(index, item) {
    return item ? item.roomNo : undefined;
  }

  ngOnDestroy(): void {
    if (this.roomSub) {
      clearInterval(this.roomSub);
    }
  }
}
