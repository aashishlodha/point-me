import { Component, Input, Output, EventEmitter, AfterViewInit, OnChanges } from '@angular/core';
import { Topic } from '../../models/topic.model';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-topic-edit',
  templateUrl: './topic-edit.component.html',
  styleUrls: ['./topic-edit.component.css']
})
export class TopicEditComponent {

  topicInstance: Topic;
  cards: string[] = [];
  @Output() topicChanged: EventEmitter<Topic> = new EventEmitter();

  get topic() {
    return this.topicInstance;
  }

  @Input()
  set topic(topic: Topic) {
    this.topicInstance = topic;
    this.cards = this.topicInstance.cards.split(',');
  }

  constructor(public activeModal: NgbActiveModal) {}

  updateTopic() {
    this.topic.cards = this.cards.join(',');
    this.topicChanged.emit(this.topic);
    this.activeModal.close();
  }

  closePopup() {
    this.activeModal.close();
  }

  addCardValueInSetting(value: string) {
    this.cards.push(value);
  }

  removeCardInSetting(index: number) {
    this.cards.splice(index, 1);
  }

}
