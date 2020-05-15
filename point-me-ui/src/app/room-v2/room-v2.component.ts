import { Component, OnInit, OnDestroy, ViewChild, AfterViewInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { NgbModal, ModalDismissReasons } from '@ng-bootstrap/ng-bootstrap';
import { RxStompService } from '@stomp/ng2-stompjs';
import { Message } from '@stomp/stompjs';
import { cardInOutAnimation } from '../shared/animations/card-animations';
import { Card } from './room.model';
import { Subscription } from 'rxjs';
import { UserService } from '../shared/services/user.service';
import { environment } from 'src/environments/environment';
import { Room } from '../shared/models/room.model';
import { ToastService } from '../shared/services/toast.service';

@Component({
  selector: 'app-room',
  templateUrl: './room-v2.component.html',
  styleUrls: ['./room.component.css']
})
export class RoomV2Component implements OnInit, OnDestroy {
  roomParamSub: Subscription;
  roomId: string;
  room: Room;
  isLoading = true;
  errorMessage: string;

  constructor(private rxStompService: RxStompService, private route: ActivatedRoute,
              private userService: UserService, private http: HttpClient,
              private modalService: NgbModal, private router: Router,
              private toastService: ToastService) { }

  ngOnInit() {
    this.roomParamSub = this.route.params.subscribe(params => {
      console.log('subscription of params');
      this.roomId = params.id;
      this.getRoomData();
    });
  }

  getRoomData() {
    this.isLoading = true;
    this.http.get<Room>(environment.baseApi + 'v2/rooms/' + this.roomId).subscribe((room) => {
      this.room = room;
      this.userService.updateIsOwner(this.room.ownerId);
      this.isLoading = false;
    }, (response) => {
      console.error(response);
      this.isLoading = false;
      switch (response.status) {
        case 404:
          console.log('Room Not Found!');
          this.errorMessage = response.error.message;
          setTimeout(() => {
            this.router.navigate(['/dashboard']);
          }, 5000);
          break;
      }
    });
  }

  copyToClipboard(val: string) {
    const selBox = document.createElement('input');
    selBox.style.display = 'hidden';
    selBox.value = val;
    document.body.appendChild(selBox);
    selBox.focus();
    selBox.select();
    document.execCommand('copy');
    document.body.removeChild(selBox);
    this.toastService.show('Room Number Copied!');
  }

  refresh() {

  }

  ngOnDestroy() {
    console.log('Destroying room');
    if (this.roomParamSub) {
      this.roomParamSub.unsubscribe();
    }
  }

}
