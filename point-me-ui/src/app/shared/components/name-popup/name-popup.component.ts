import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Participant } from '../../models/participant.model';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-name-popup',
  templateUrl: './name-popup.component.html',
  styleUrls: ['./name-popup.component.css']
})
export class NamePopupComponent {

  @Input() user: Participant;
  @Output() nameChanged: EventEmitter<any> = new EventEmitter();

  constructor(public activeModal: NgbActiveModal) { }

  changeName() {
    console.log('changeName', this.user);
    this.nameChanged.emit(this.user.name);
    this.activeModal.close();
  }

}
