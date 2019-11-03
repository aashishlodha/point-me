import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PlayingCardComponent } from './components/playing-card/playing-card.component';
import { RoomService } from './services/room.service';
import { UserService } from './services/user.service';

@NgModule({
  declarations: [
    PlayingCardComponent
  ],
  imports: [
    CommonModule
  ],
  providers: [
    RoomService,
    UserService
  ],
  exports: [
    PlayingCardComponent
  ]
})
export class SharedModule { }
