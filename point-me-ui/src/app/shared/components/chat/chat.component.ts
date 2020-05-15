import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { RxStompService } from '@stomp/ng2-stompjs';
import { Message } from '@stomp/stompjs';
import { environment } from 'src/environments/environment';
import { UserService } from '../../services/user.service';
import { Participant } from '../../models/participant.model';
import { ChatMessage } from '../../models/message.model';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css']
})
export class ChatComponent implements OnInit, OnDestroy {

  @Input() topicId: string;
  chatMessageInput: string;
  chatMessages: ChatMessage[];
  chatSub: Subscription;
  user: Participant;

  constructor(
    private userService: UserService,
    private http: HttpClient,
    private rxStompService: RxStompService
  ) { }

  ngOnInit() {
    console.log('Chat component got created...');
    this.user = this.userService.user;
    this.http.get<ChatMessage[]>(environment.baseApi + 'v2/topics/' + this.topicId + '/chat')
      .subscribe((messages) => {
        console.log('Chat message was pushed successfully...');
        this.chatMessages = messages;
        if (!this.chatMessages) {
          this.chatMessages = [];
        }
      });
    this.chatSub = this.rxStompService.watch('/topic/topics/' + this.topicId + '/chat')
      .subscribe((message: Message) => {
        let msg: ChatMessage = null;
        if (message) {
          msg = JSON.parse(message.body);
        }
        this.chatMessages.push(msg);
      });
  }

  sendChatMessage() {
    const message: ChatMessage = {
      byId: this.user.id,
      byName: this.user.name,
      text: this.chatMessageInput,
      topicId: this.topicId
    };
    this.rxStompService.publish({
      destination: '/app/topics/' + this.topicId + '/chat',
      body: JSON.stringify(message)
    });
    this.chatMessageInput = '';
  }

  ngOnDestroy(): void {
    console.log('Chat component got destroyed...');
    if (this.chatSub) {
      this.chatSub.unsubscribe();
    }
  }

}
