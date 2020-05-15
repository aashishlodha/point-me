import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { UserService } from '../shared/services/user.service';
import { Room } from '../shared/models/room.model';
import { Participant } from '../shared/models/participant.model';

@Component({
  selector: 'app-reception',
  templateUrl: './reception.component.html',
  styleUrls: ['./reception.component.css']
})
export class ReceptionComponent implements OnInit {

  public roomId: string;

  constructor(private userService: UserService, private router: Router, private http: HttpClient) { }

  ngOnInit() {
  }

  async getRoom() {
    await this.userService.openNameModal();

    this.http.post<Participant>(environment.baseApi + 'v2/participants', this.userService.user)
      .toPromise().then((participant) => {
        this.userService.updateUser(participant);

        this.http.post<Room>(environment.baseApi + '/v2/rooms', this.userService.user).subscribe((room: any) => {
          if (!room) {
            alert('No Room Available...');
          } else {

            this.router.navigate(['/v2/room', room.id]);
          }
        }, (err) => {
          console.error(err);
        });
      });
  }

  joinRoom() {
    this.router.navigate(['/v2/room', this.roomId]);
  }

}
