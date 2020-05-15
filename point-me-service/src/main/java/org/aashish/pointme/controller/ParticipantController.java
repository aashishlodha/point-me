package org.aashish.pointme.controller;

import java.util.List;

import org.aashish.pointme.entity.v2.Participant;
import org.aashish.pointme.entity.v2.Room;
import org.aashish.pointme.exception.ResourceNotFoundException;
import org.aashish.pointme.service.ParticipantService;
import org.aashish.pointme.service.RoomV2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v2")
public class ParticipantController {
	
	@Autowired
	private RoomV2Service roomService;
	
	@Autowired
	private ParticipantService participantService;
	
	@GetMapping("/rooms/{roomId}/participants")
    public List<Participant> getParticipants(@PathVariable String roomId) {

		Room room = roomService.findRoomById(roomId);
        if(room != null) {
            return participantService.getParticipantsByRoom(room);
        }
        return null;
    }

	@PostMapping("/rooms/{roomId}/participants")
    public Participant joinRoom(@PathVariable String roomId, @RequestBody Participant participant) {

        Room room = roomService.findRoomById(roomId);
        if(room != null) {
        	return participantService.registerParticipant(room, participant.getName(), participant.getId());
        } else {
            throw new ResourceNotFoundException("Room " + roomId + " not found");
        }
    }
	
	@PostMapping("/participants")
    public Participant registerParticipant(@RequestBody Participant participant) {
        return participantService.registerParticipant(null, participant.getName(), participant.getId());
    }
	
}
