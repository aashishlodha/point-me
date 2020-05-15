import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ToastService {

  toasts: string[] = [];

  constructor() { }

  show(body: string) {
    this.toasts.push(body);
  }

  remove(toast) {
    this.toasts = this.toasts.filter(t => t !== toast);
  }
}
