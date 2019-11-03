import { trigger, transition, style, animate } from '@angular/animations';

export const cardInOutAnimation = trigger('cardInOutAnimation', [
    transition(':enter', [
        style({ transform: 'translateX(100%) translateY(100%) rotate(180deg)' }),
        animate('0.6s ease-out')
    ]),
    transition(':leave', [
        animate('0.6s ease-out', style({ transform: 'translateY(100%)', opacity: 0 }))
    ])
]);
