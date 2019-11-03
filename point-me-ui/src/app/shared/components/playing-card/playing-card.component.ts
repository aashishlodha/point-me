import { Component, OnInit, Input, EventEmitter, Output, OnChanges, SimpleChanges } from '@angular/core';

@Component({
  selector: 'app-playing-card',
  templateUrl: './playing-card.component.html',
  styleUrls: ['./playing-card.component.scss']
})
export class PlayingCardComponent implements OnInit, OnChanges {

  @Input() value: number;
  @Input() selectedCard: number;
  @Output() selected = new EventEmitter<number>();

  constructor() { }

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges) {
    // console.log(changes);
    if (changes && changes.selectedCard) {
      this.selectedCard = changes.selectedCard.currentValue;
    }
  }

  onSelection() {
    this.selected.emit(this.value);
  }

}
