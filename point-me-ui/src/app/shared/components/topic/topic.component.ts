import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { RxStompService } from '@stomp/ng2-stompjs';
import { Message } from '@stomp/stompjs';
import { NgbModal, NgbModalOptions } from '@ng-bootstrap/ng-bootstrap';
import { Topic } from '../../models/topic.model';
import { environment } from 'src/environments/environment';
import { UserService } from '../../services/user.service';
import { TopicEditComponent } from '../topic-edit/topic-edit.component';

@Component({
  selector: 'app-topic',
  templateUrl: './topic.component.html',
  styleUrls: ['./topic.component.css']
})
export class TopicComponent implements OnInit, OnDestroy {

  @Input() topicId: string;
  topic: Topic;
  isOwner = false;
  topicUpdateSubscription: Subscription;
  cards: string[];
  selectedCard: string;
  results: any;

  constructor(private userService: UserService,
              private rxStompService: RxStompService, private http: HttpClient,
              private modalService: NgbModal) { }

  ngOnInit() {
    console.log('subscribing to topic');
    if (this.topicUpdateSubscription) {
      this.topicUpdateSubscription.unsubscribe();
    }
    this.topicUpdateSubscription = this.rxStompService.watch('/topic/topics/' + this.topicId)
      .subscribe((message: Message) => {
        console.log('topic change received', message);
        const updatedTopic = JSON.parse(message.body);
        if (this.topic.id !== updatedTopic.id) {
          console.log('Topic changed');
          this.selectedCard = null;
        }
        if (updatedTopic.state === 'IN_RESULTS') {
          this.getResults();
        }
        if (this.topic.cards !== updatedTopic.cards) {
          this.cards = updatedTopic.cards.split(',');
        }
        this.topic = updatedTopic;
      });

    this.getTopicInfo();
  }

  getResults() {
    this.http.get(environment.baseApi + 'v2/topics/' + this.topicId + '/results')
      .subscribe((results: any) => {
        this.results = results;
      });
  }

  getTopicInfo() {
    this.http.get<Topic>(environment.baseApi + 'v2/topics/' + this.topicId).subscribe((topic) => {
      console.log('topic data received', topic);
      this.topic = topic;
      this.isOwner = this.userService.getIsOwner();
      this.cards = topic.cards.split(',');
      if (topic.state === 'IN_VOTING') {
        this.getCastedVote();
      } else if (topic.state === 'IN_RESULTS') {
        this.getResults();
      }
    });
  }

  updateTopic(updatedTopic: any) {
    this.http.put(environment.baseApi + 'v2/topics/' + this.topicId, updatedTopic).subscribe(() => {
      console.log('Pushed topic changes successfully...');
    });
  }

  getCastedVote() {
    this.http.get<string>(environment.baseApi + 'v2/topics/' + this.topicId + '/votes/' + this.userService.user.id)
    .subscribe((voteValue) => {
      this.selectedCard = voteValue;
    });
  }

  openTopic() {
    const modalOption: NgbModalOptions = {
      backdrop: 'static',
      keyboard: false
    };
    const modalRef = this.modalService.open(TopicEditComponent, modalOption);

    modalRef.componentInstance.topic = {...this.topic};

    const nameSub = modalRef.componentInstance.topicChanged.subscribe((updatedTopic: Topic) => {
      console.log('Topic change subscriber', updatedTopic);
      this.updateTopic(updatedTopic);
    });
  }

  startVoting() {
    console.log('Start Voting clicked');
    const updatedTopic = {...this.topic};
    updatedTopic.state = 'IN_VOTING';
    this.http.put(environment.baseApi + 'v2/topics/' + this.topicId, updatedTopic)
      .subscribe((topic) => {
        console.log('Voting event published...', topic);
      });
  }

  showVotes() {
    console.log('Show Votes clicked');
    this.topic.state = 'IN_RESULTS';
    this.http.put(environment.baseApi + 'v2/topics/' + this.topicId, this.topic)
      .subscribe((topic) => {
        console.log('Voting stopped..', topic);
      });
  }

  reset() {
    console.log('reset clicked');
    this.topic.state = 'IS_FINISHED';
    this.http.put(environment.baseApi + 'v2/topics/' + this.topicId, this.topic)
      .subscribe((topic) => {
        console.log('reset topic..', topic);
      });
  }

  discard() {
    console.log('discard clicked');
    this.topic.state = 'IS_DISCARDED';
    this.http.put(environment.baseApi + 'v2/topics/' + this.topicId, this.topic)
      .subscribe((topic) => {
        console.log('Discard topic..', topic);
      });
  }

  handleCardSelection(event) {
    console.log('handleCardSelection', event);
    this.selectedCard = event;
    const vote = {
      casterId: this.userService.user.id,
      casterName: this.userService.user.name,
      voteValue: this.selectedCard
    };
    this.http.post(environment.baseApi + 'v2/topics/' + this.topicId + '/vote', vote).subscribe((castedVote) => {
      console.log('Casted vote successfully', castedVote);
    });
  }

  ngOnDestroy(): void {
    if (this.topicUpdateSubscription) {
      this.topicUpdateSubscription.unsubscribe();
    }
  }

}
