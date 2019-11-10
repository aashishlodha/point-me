import { Component, OnInit, OnDestroy, ViewChild, AfterViewInit } from '@angular/core';
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
export class RoomComponent implements OnInit, AfterViewInit, OnDestroy {

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

  participants: any[];
  selectedCard: number;
  closeResult: string;
  cardsInSetting: Card[];
  nameInSetting: string;
  @ViewChild('nameChangeModal', { static: false }) nameChangeModal;

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

  ngAfterViewInit(): void {
    if (this.user.name === 'Anonymous User') {
      this.openNameModal(this.nameChangeModal);
    }
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
    this.cardsInSetting = [...this.room.cards];
    this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title'}).result.then((result) => {
      this.rxStompService.publish({
        destination: '/app/rooms/' + this.roomNo + '/cards',
        body: JSON.stringify(this.cardsInSetting)
      });
      // this.closeResult = `Closed with: ${result}`;
    }, (reason) => {
      this.cardsInSetting = null;
      // this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
    });
  }

  openNameModal(content) {
    this.nameInSetting = '' + this.user.name;
    this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title'}).result.then((result) => {
      console.log('name changed to ' + this.nameInSetting);
      this.user.name = this.nameInSetting;
      this.userNameChanged();
      // this.closeResult = `Closed with: ${result}`;
    }, (reason) => {
      this.nameInSetting = null;
      // this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
    });
  }

  public removeCardInSetting(index) {
    this.cardsInSetting.splice(index, 1);
  }

  public addCardValueInSetting(value) {
    if (value && Number(value)) {
      value = Number(value);
      const result = this.cardsInSetting.find((card) => value === card.value);
      if (result) {
        console.log('card already present!');
        return;
      } else {
        this.cardsInSetting.push({value});
        this.cardsInSetting.sort((leftSide, rightSide) => {
          return leftSide.value - rightSide.value;
        });
      }
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
