import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  user;

  constructor() {
    if (localStorage) {
      const storedUser = localStorage.getItem('pointMeUser');
      if (storedUser) {
        this.user = JSON.parse(storedUser);
      } else {
        this.user = {
          id: (new Date()).getTime(),
          name: 'Anonymous User'
        };
        localStorage.setItem('pointMeUser', JSON.stringify(this.user));
      }
    } else {
      this.user = {
        id: (new Date()).getTime(),
        name: 'Anonymous User'
      };
    }
  }

  checkIfUserExists(users: any[]) {
    const result = users.find(({id}) => id === this.user.id);
    return result ? true : false;
  }
}
