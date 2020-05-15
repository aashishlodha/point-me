package org.aashish.pointme.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.aashish.pointme.entity.v2.Connection;
import org.aashish.pointme.entity.v2.Participant;
import org.aashish.pointme.entity.v2.ResourceAction;
import org.aashish.pointme.entity.v2.Room;
import org.aashish.pointme.repository.ConnectionRepository;
import org.aashish.pointme.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParticipantService {
	
	@Autowired
	public ParticipantRepository participantRepo;
	
	@Autowired
	private NotificationService notService;
	
	@Autowired
	private ConnectionRepository connRepo;
	
	public Participant registerParticipant(Room room, String name, String id) {
		Participant participant = null;
		if(id == null || id.isEmpty()) {			
			participant = new Participant();
			participant.setId(UUID.randomUUID().toString());
		} else {
			Optional<Participant> findById = participantRepo.findById(id);
			if(findById.isPresent()) {				
				participant = findById.get();
			} else {
				participant = new Participant();
				participant.setId(id);
			}
		}
		participant.setName(name);
		participant.setRoom(room);
		participant = participantRepo.save(participant);
		
		// Create delta change for participant..
		ResourceAction<Participant> partAction = new ResourceAction<Participant>();
		partAction.setType("add");
		partAction.setId(participant.getId());
		partAction.setBody(participant);
		
		if(room != null) {			
			notService.notifyRoomParticipants(room.getId(), partAction);
		}
        return participant;
	}

	public List<Participant> getParticipantsByRoom(Room room) {
		return participantRepo.findAllByRoom(room);
	}
	
	public List<Participant> getAllParticipants() {
		return participantRepo.findAll();
	}
	
	public void addConnection(String participantId, String simpSessionId) {
		Optional<Connection> findById = connRepo.findById(simpSessionId);
		Connection connection = null;
		if(findById.isPresent()) {
			connection = findById.get();
		} else {
			connection = new Connection();
		}
		connection.setId(simpSessionId);
		connection.setParticipantId(participantId);
		connection = connRepo.save(connection);
	}
	
	public void removeConnection(String simpSessionId) {
		Optional<Connection> findById = connRepo.findById(simpSessionId);
		String participantId = null;
		if(findById.isPresent()) {
			Connection conn = findById.get();
			participantId = conn.getParticipantId();
			connRepo.delete(conn);
		}
		
		if(participantId != null) {
			List<Connection> connections = connRepo.findAllByParticipantId(participantId);
			
			if(connections.isEmpty()) {
				Optional<Participant> participantResult = participantRepo.findById(participantId);
				if(participantResult.isPresent()) {
					Participant participant = participantResult.get();
					Room room = participant.getRoom();
//					participantRepo.delete(participant);
					participant.setRoom(null);
					participantRepo.save(participant);
					
					// Create delta change for participant..
					ResourceAction<Participant> partAction = new ResourceAction<Participant>();
					partAction.setType("remove");
					partAction.setId(participant.getId());
					partAction.setBody(null);
					
					notService.notifyRoomParticipants(room.getId(), partAction);
				}
			}
		}
	}

	public void notifyVoted(String casterId) {
		Optional<Participant> optional = participantRepo.findById(casterId);
		
		if(optional.isPresent()) {
			Participant participant = optional.get();
			if(!participant.isVoted()) {
				participant.setVoted(true);
				participantRepo.save(participant);
				
				// Create delta change for participant..
				ResourceAction<Participant> partAction = new ResourceAction<Participant>();
				partAction.setType("update");
				partAction.setId(participant.getId());
				partAction.setBody(participant);
				
				notService.notifyRoomParticipants(participant.getRoom().getId(), partAction);
			}
		}
		
	}

	public void resetParticipantVotingState(Room room) {
		List<Participant> participants = participantRepo.findAllByRoom(room);
		participants.forEach(participant -> {
			participant.setVoted(false);
		});
		participantRepo.saveAll(participants);
		// Create delta change for participant..
		ResourceAction<Participant> partAction = new ResourceAction<Participant>();
		partAction.setType("update-all");
		partAction.setId(null);
		partAction.setBody(null);
		
		notService.notifyRoomParticipants(room.getId(), partAction);
	}
}
