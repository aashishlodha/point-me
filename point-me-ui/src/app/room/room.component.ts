import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { NgbModal, ModalDismissReasons } from '@ng-bootstrap/ng-bootstrap';
import { RxStompService } from '@stomp/ng2-stompjs';
import { Message } from '@stomp/stompjs';
import { cardInOutAnimation } from '../shared/animations/card-animations';
import { Card } from './room.model';
import { Subscription } from 'rxjs';
import { UserService } from '../shared/services/user.service';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-room',
  templateUrl: './room.component.html',
  styleUrls: ['./room.component.css'],
  animations: [
    cardInOutAnimation
  ]
})
export class RoomComponent implements OnInit, OnDestroy {

  public room: any;
  roomNo: number;
  roomSubscription: Subscription;
  participantsSubscription: Subscription;
  chatSubscription: Subscription;
  roomEventSubscription: Subscription;
  resultsSubscription: Subscription;
  user;
  roomOwner;
  userName = '';
  firstLoad = true;
  public chatMessageInput: string;
  chatMessages: any[] = [];
  roomMode = 'default';
  results: any;

  cards: Card[];
  participants: any[];
  selectedCard: number;
  closeResult: string;

  constructor(private rxStompService: RxStompService, private route: ActivatedRoute,
              private userService: UserService, private http: HttpClient,
              private modalService: NgbModal) { }

  ngOnInit() {
    // this.selectedCard = +localStorage.getItem('selectedCard');
    this.user = this.userService.user;
    this.userName = this.user.name;
    // this.roomNo = +this.route.snapshot.paramMap.get('id');
    this.route.params.subscribe(params => {
      console.log('subscription of params');
      if (this.roomNo && this.roomNo !== params.id) {
        location.reload();
      }
      this.roomNo = params.id;
      if (this.roomSubscription) {
        this.ngOnDestroy();
      }

      this.roomSubscription = this.rxStompService.watch('/topic/rooms/' + this.roomNo).subscribe((message: Message) => {
        console.log(message);
        this.room = JSON.parse(message.body);
        this.roomMode = this.room.status;
        this.roomOwner = this.room.roomOwner;
        if (!this.firstLoad) {
          if (!this.userService.checkIfUserExists(this.participants)) {
            // location.reload();
            return;
          }
        }
        this.firstLoad = false;
      });

      this.participantsSubscription = this.rxStompService.watch('/topic/rooms/' + this.roomNo + '/participants')
      .subscribe((message: Message) => {
        console.log(message);
        this.participants = JSON.parse(message.body);
        // this.roomOwner = this.participants[0];
        if (this.roomMode === 'voting') {
          this.participants.forEach((participant) => {
            if (this.user.id === participant.id) {
              this.selectedCard = participant.voteValue;
            }
          });
        }
        // this.participants = users.filter(usr => usr.id !== this.user.id);
      });

      this.chatSubscription = this.rxStompService.watch('/topic/rooms/' + this.roomNo + '/chat').subscribe((message: Message) => {
        console.log(message);
        const chat = JSON.parse(message.body);
        this.chatMessages = chat;
      });

      this.roomEventSubscription = this.rxStompService.watch('/topic/rooms/' + this.roomNo + '/event').subscribe((message: Message) => {
        // localStorage.removeItem('selectedCard');
        this.selectedCard = null;
        console.log(message);
        const msg = message.body;
        this.roomMode = msg;
      });

      this.resultsSubscription = this.rxStompService.watch('/topic/rooms/' + this.roomNo + '/results').subscribe((message: Message) => {
        this.results = JSON.parse(message.body);
        console.log('Voting results: ', this.results);
      });

      this.joinToRoom();
    });
  }

  joinToRoom() {
    console.log(JSON.stringify(this.user));
    this.rxStompService.publish({
      destination: '/app/rooms/' + this.roomNo + '/join',
      body: JSON.stringify(this.user)
    });
    this.http.get(environment.baseApi + 'rooms/' + this.roomNo + '/chat')
      .subscribe((data) => {
        this.chatMessages = data as [];
      });
  }

  startVoting() {
    console.log('starting voting...');
    // localStorage.removeItem('selectedCard');
    this.selectedCard = null;
    this.rxStompService.publish({
      destination: '/app/rooms/' + this.roomNo + '/start-voting'
    });
  }

  showVotes() {
    console.log('stoping voting...');
    this.rxStompService.publish({
      destination: '/app/rooms/' + this.roomNo + '/show-results'
    });
  }

  reset() {
    console.log('resetting room...');
    this.rxStompService.publish({
      destination: '/app/rooms/' + this.roomNo + '/reset'
    });
  }

  sendChatMessage() {
    const chatMessage = {
      message: this.chatMessageInput,
      user: this.user
    };
    this.rxStompService.publish({
      destination: '/app/rooms/' + this.roomNo + '/chat',
      body: JSON.stringify(chatMessage)
    });
    this.chatMessageInput = '';
  }

  userNameChanged() {
    console.log('user name changed to', this.user.name);
    this.userService.user = this.user;
    this.rxStompService.publish({
      destination: '/app/rooms/' + this.roomNo + '/participant/update',
      body: JSON.stringify(this.user)
    });
    localStorage.setItem('pointMeUser', JSON.stringify(this.user));
  }

  handleCardSelection(val) {
    this.selectedCard = val;
    const vote = {
      value: val,
      user: this.user
    };
    // localStorage.setItem('selectedCard', val);
    this.rxStompService.publish({
      destination: '/app/rooms/' + this.roomNo + '/cast-vote',
      body: JSON.stringify(vote)
    });
  }

  refresh() {
    location.reload();
  }

  open(content) {
    this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title'}).result.then((result) => {
      this.closeResult = `Closed with: ${result}`;
    }, (reason) => {
      this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
    });
  }

  private getDismissReason(reason: any): string {
    if (reason === ModalDismissReasons.ESC) {
      return 'by pressing ESC';
    } else if (reason === ModalDismissReasons.BACKDROP_CLICK) {
      return 'by clicking on a backdrop';
    } else {
      return  `with: ${reason}`;
    }
  }

  ngOnDestroy() {
    console.log('Destroying room');
    this.rxStompService.publish({
      destination: '/app/rooms/' + this.roomNo + '/disconnect',
      body: JSON.stringify(this.user)
    });
    if (this.roomSubscription) {
      this.roomSubscription.unsubscribe();
    }
    if (this.chatSubscription) {
      this.chatSubscription.unsubscribe();
    }
    if (this.roomEventSubscription) {
      this.roomEventSubscription.unsubscribe();
    }
    if (this.participantsSubscription) {
      this.participantsSubscription.unsubscribe();
    }
    if (this.resultsSubscription) {
      this.resultsSubscription.unsubscribe();
    }
  }

}
