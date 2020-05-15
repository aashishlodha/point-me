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

@NgModule({
  declarations: [
    PlayingCardComponent,
    ParticipantsComponent,
    ChatComponent,
    TopicComponent,
    NamePopupComponent,
    TopicEditComponent
  ],
  imports: [
    CommonModule,
    FormsModule
  ],
  providers: [
    RoomService,
    UserService
  ],
  exports: [
    PlayingCardComponent,
    ParticipantsComponent,
    ChatComponent,
    TopicComponent,
    NamePopupComponent,
    TopicEditComponent
  ],
  entryComponents: [NamePopupComponent, TopicEditComponent]
})
export class SharedModule { }
