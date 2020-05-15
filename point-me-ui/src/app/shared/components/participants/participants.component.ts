import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { UserService } from '../../services/user.service';
import { Subscription } from 'rxjs';
import { Message } from '@stomp/stompjs';
import { RxStompService } from '@stomp/ng2-stompjs';
import { Participant } from '../../models/participant.model';
import { environment } from 'src/environments/environment';
import { ResourceAction } from '../../models/resource-action.model';

@Component({
  selector: 'app-participants',
  templateUrl: './participants.component.html',
  styleUrls: ['./participants.component.css']
})
export class ParticipantsComponent implements OnInit, OnDestroy {

  roomSub: Subscription;
  roomId: string;
  participantsSubscription: Subscription;
  participants: Participant[];
  user: Participant;

  constructor(
    private rxStompService: RxStompService,
    private route: ActivatedRoute,
    private userService: UserService,
    private http: HttpClient,
    private modalService: NgbModal) { }

  ngOnInit() {
    this.roomSub = this.route.params.subscribe(params => {
      console.log('subscription of params');

      this.roomId = params.id;

      console.log('subscribing to participants topic', this.rxStompService);

      this.participantsSubscription = this.rxStompService.watch('/topic/rooms/' + this.roomId + '/participants')
        .subscribe((message: Message) => {
          console.log('participants change received', message);
          const action: ResourceAction <Participant> = JSON.parse(message.body);

          if (this.participants == null) {
            this.participants = [];
          }

          switch (action.type) {
            case 'add' :
              console.log('adding newly joined participant...');
              this.participants = this.participants.filter((record) => {
                return record.id !== action.id;
              });
              this.participants.push(action.body);
              break;
            case 'update':
              console.log('updating an participant...');
              const participant = this.participants.find((record) => record.id === action.id);
              participant.name = action.body.name;
              participant.voted = action.body.voted;
              break;
            case 'remove':
              console.log('removing participant from the list...');
              this.participants = this.participants.filter((record) => {
                return record.id !== action.id;
              });
              break;
            case 'update-all':
              console.log('refreshing participant list');
              this.getAllParticipants();
          }
        });

      this.joinToRoom();
    });
  }

  trackByFn(index: number, part: Participant): string {
    return part.id;
  }

  async joinToRoom() {
    await this.userService.openNameModal();
    console.log('joining to room');
    const user = this.userService.user;
    const body = {
      id: user.id,
      name: user.name
    };

    this.http.post<Participant>(environment.baseApi + 'v2/rooms/' + this.roomId + '/participants', body).subscribe(
      (participant) => {
        console.log('Joined room', participant);
        this.user = participant;
        this.userService.updateUser(this.user);
        this.getAllParticipants();
        this.rxStompService.publish({
          destination: '/app/rooms/' + this.roomId + '/participants',
          body: JSON.stringify(this.user)
        });
      },
      (e) => { console.error(e); }
    );
  }

  getAllParticipants() {
    this.http.get<Participant[]>(environment.baseApi + 'v2/rooms/' + this.roomId + '/participants').subscribe((participants) => {
      this.participants = participants;
    });
  }

  ngOnDestroy() {
    console.log('Destroying room');
    if (this.participantsSubscription) {
      this.participantsSubscription.unsubscribe();
    }
    if (this.roomSub) {
      this.roomSub.unsubscribe();
    }
  }

}
