import { Injectable } from '@angular/core';
import { Room } from 'src/app/room/room.model';

@Injectable({
  providedIn: 'root'
})
export class RoomService {

  // rooms: Room[];

  constructor() {
    // this.rooms = new Array();

    // // TODO: remove after back end implementation
    // this.createTestRooms();
    // console.info(this.rooms);
  }

  // createTestRooms() {
  //   for (let i = 1; i <= 10; i++) {
  //     this.rooms.push(new Room(i));
  //   }
  // }

  // getNextAvailableRoom() {
  //   for (let i = 0; i <= this.rooms.length; i++) {
  //     if (!this.rooms[i]['roomOwner']) {
  //       return this.rooms[i].roomNo;
  //     }
  //   }
  //   return 0;
  // }

  // joinRoom(roomNumber: number) {
  //   const room = this.rooms[roomNumber - 1];
  //   room.isOccupied = true;
  //   if (!room.owner) {
  //     console.log('No owner found!');
  //   }
  // }

}
