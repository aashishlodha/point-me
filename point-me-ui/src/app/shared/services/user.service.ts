import { Injectable } from '@angular/core';
import { NgbModal, NgbModalOptions } from '@ng-bootstrap/ng-bootstrap';
import { NamePopupComponent } from '../components/name-popup/name-popup.component';
import { resolve } from 'url';
import { Participant } from '../models/participant.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  user;
  isOwner = false;
  isLoggedIn = false;
  userName: string;

  constructor(private modalService: NgbModal) {
    if (localStorage) {
      const storedUser = localStorage.getItem('pointMeUser');
      if (storedUser) {
        this.user = JSON.parse(storedUser);
      }
    }
    if (!this.user) {
      this.user = {
        id: null,
        name: 'Anonymous User'
      };
    }
  }

  updateUser(user: Participant) {
    this.user = user;
    localStorage.setItem('pointMeUser', JSON.stringify(this.user));
  }

  updateIsOwner(ownerId: string) {
    this.isOwner = ownerId === this.user.id;
  }

  getIsOwner() {
    return this.isOwner;
  }

  getIsLoggedIn() {
    return this.isLoggedIn;
  }

  getUserName() {
    return this.userName;
  }

  checkIfUserExists(users: any[]) {
    const result = users.find(({id}) => id === this.user.id);
    return result ? true : false;
  }

  openNameModal() {
    return new Promise(resolve => {
      if (!this.user || !this.user.id) {
        const modalOption: NgbModalOptions = {
          backdrop: 'static',
          keyboard: false
        };
        const modalRef = this.modalService.open(NamePopupComponent, modalOption);

        modalRef.componentInstance.user = this.user;

        const nameSub = modalRef.componentInstance.nameChanged.subscribe((updatedName) => {
          console.log('Name change subscriber', updatedName);
          this.updateUserName(updatedName);
          resolve(updatedName);
        });
      } else {
        resolve(this.user);
      }
    });
  }

  updateUserName(name: string) {
    this.user.name = name;
    localStorage.setItem('pointMeUser', JSON.stringify(this.user));
    console.log('user\'s name was changed...');
  }

}
