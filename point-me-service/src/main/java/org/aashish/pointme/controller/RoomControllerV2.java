package org.aashish.pointme.controller;

import javax.validation.Valid;

import org.aashish.pointme.entity.v2.Participant;
import org.aashish.pointme.entity.v2.Room;
import org.aashish.pointme.exception.ResourceNotFoundException;
import org.aashish.pointme.service.RoomV2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("v2")
public class RoomControllerV2 {

    @Autowired
    private RoomV2Service roomService;
    
    @GetMapping("/rooms/{roomId}")
    public Room getRoomInfo(@PathVariable String roomId) {
    	Room room = roomService.findRoomById(roomId);
    	if(room == null) {
    		throw new ResourceNotFoundException("Room {" + roomId + "} not found");
    	}
    	return room;
    }

    @PostMapping("/rooms")
    public Room createRoom(@Valid @RequestBody Participant owner) {
        Room room = roomService.createRoom(owner);
        return room;
    }

}