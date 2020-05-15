package org.aashish.pointme.controller;

import java.util.List;

import org.aashish.pointme.entity.v2.Participant;
import org.aashish.pointme.entity.v2.Room;
import org.aashish.pointme.service.ParticipantService;
import org.aashish.pointme.service.RoomV2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v2")
public class ReceptionController {

    @Autowired
    private RoomV2Service roomService;

    @Autowired
    private ParticipantService participantService;

    @GetMapping("/room-register")
    public List<Room> roomRegister() {
        return roomService.findAllRooms();
    }

    @GetMapping("/guest-register")
    public List<Participant> guestRegister() {
    	return participantService.getAllParticipants();
    }

}