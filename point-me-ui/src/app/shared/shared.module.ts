import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PlayingCardComponent } from './components/playing-card/playing-card.component';
import { RoomService } from './services/room.service';
import { UserService } from './services/user.service';
import { ParticipantsComponent } from './components/participants/participants.component';
import { ChatComponent } from './components/chat/chat.component';
import { FormsModule } from '@angular/forms';
import { TopicComponent } from './components/topic/topic.component';
import { NamePopupComponent } from './components/name-popup/name-popup.component';
import { TopicEditComponent } from './components/topic-edit/topic-edit.component';
import { ToastComponent } from './components/toast/toast.component';
import { NgbToastModule } from '@ng-bootstrap/ng-bootstrap';
import { ToastService } from './services/toast.service';

@NgModule({
  declarations: [
    PlayingCardComponent,
    ParticipantsComponent,
    ChatComponent,
    TopicComponent,
    NamePopupComponent,
    TopicEditComponent,
    ToastComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    NgbToastModule
  ],
  providers: [
    RoomService,
    UserService,
    ToastService
  ],
  exports: [
    PlayingCardComponent,
    ParticipantsComponent,
    ChatComponent,
    TopicComponent,
    NamePopupComponent,
    TopicEditComponent,
    ToastComponent
  ],
  entryComponents: [NamePopupComponent, TopicEditComponent]
})
export class SharedModule { }
