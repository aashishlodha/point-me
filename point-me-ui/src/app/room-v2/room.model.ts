export class User {
    public id: string;
    public displayName: string;
}

export class RoomConfiguration {
    public name: string;
    public description: string;
    public isPasswordProtected: boolean;
    public maxNumberOfPlayers: number;
    public cards: Card[];

    constructor() {
        this.name = 'Point Me Room';
        this.description = 'Pointing estimation session';
        this.isPasswordProtected = false;
        this.maxNumberOfPlayers = 10;
    }
}

export class Room {
    public roomNo: number;
    public config: RoomConfiguration;
    public owner: User;
    public participants: User[];
    public isOccupied: boolean;

    constructor(roomNo: number) {
        this.roomNo = roomNo;
        this.config = new RoomConfiguration();
        this.isOccupied = false;
    }
}

export class Card {
    value: any;
    constructor(value: any) {
        this.value = value;
    }
}
