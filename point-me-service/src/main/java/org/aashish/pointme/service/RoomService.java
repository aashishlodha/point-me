package org.aashish.pointme.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.aashish.pointme.dto.Card;
import org.aashish.pointme.dto.ChatMessage;
import org.aashish.pointme.dto.Room;
import org.aashish.pointme.dto.User;
import org.aashish.pointme.dto.Vote;
import org.springframework.stereotype.Service;

@Service
public class RoomService {
	
	private Map<String, Room> rooms = new HashMap<>();

	public Map<String, Room> findAllRooms() {
		return this.rooms;
	}
	
	public Room findRoomById(String id) {
		return this.rooms.get(id);
	}
	
	public Room createRoom() {
		Room room = new Room();
		this.rooms.put(room.getRoomId(), room);
		return room;
	}
	
	public void joinRoom(String roomId, User user) {
		Room room = rooms.get(roomId);
		if(room.getRoomOwner() == null) {
			room.setRoomOwner(user);
		}
		// TODO remove if not needed
		if(room.getStatus().equals("voting") || room.getStatus().equals("results")) {
			user.setVoteValue(room.getVotingBox().get(user.getId()));
		}
		// TODO remove if not needed
		for(User participant : this.rooms.get(roomId).getParticipants()) {
			if(participant.getId().equals(user.getId())) {
				participant.setVoteValue(user.getVoteValue());
				return;
			}
		}
		this.rooms.get(roomId).getParticipants().add(user);
	}
	
	public void disconnectRoom(String roomId, User user) {
		Room room = this.rooms.get(roomId);
		List<User> currentList = room.getParticipants();
		List<User> newList = new ArrayList<>();
		for(User participant : currentList) {
			if(!participant.getId().equals(user.getId())) {
				newList.add(participant);
			}
		}
		room.setParticipants(newList);
		if(newList.size() == 0) {
//			room.setRoomOwner(null);
//			room.setRoomDesc(null);
//			room.setCards(defaultCards);
//			room.setVoteCounts(null);
//			room.setVotingBox(null);
//			room.setChat(null);
			this.rooms.put(roomId, new Room());
		}
	}
	
	public List<User> updateParticipant(String roomId, User user) {
		List<User> participants = this.rooms.get(roomId).getParticipants();
		for(User participant : participants) {
			if(participant.getId().equals(user.getId())) {
				participant.setName(user.getName());
				break;
			}
		}
		return participants;
	}

	public void createVotingBox(String roomId) {
		Map<Long, Integer> votingBox = new HashMap<>();
		this.rooms.get(roomId).setVotingBox(votingBox);
	}

	public void castVote(String roomId, Vote vote) {
		User user = vote.getUser();
		List<User> participants = this.rooms.get(roomId).getParticipants();
		for(User participant : participants) {
			if(user.getId().equals(participant.getId())) {
				participant.setVoteValue(vote.getValue());
				break;
			}
		}
		this.rooms.get(roomId).getVotingBox().put(user.getId(), vote.getValue());
	}

	public Map<Integer, Integer> countVotes(String roomId) {
		Map<Long, Integer> votingBox = this.rooms.get(roomId).getVotingBox();
		Map<Integer, Integer> results = new HashMap<>();
		for(Entry<Long, Integer> entry : votingBox.entrySet()) {
			if(results.containsKey(entry.getValue())) {
				results.put(Integer.valueOf(entry.getValue().toString()), results.get(entry.getKey()) + 1);
			} else {
				results.put(Integer.parseInt(entry.getValue().toString()), 1);
			}
		}
		
		this.rooms.get(roomId).setVoteCounts(results);
		return results;
	}

	public void clearParticipantVotingByRoom(String roomId) {
		List<User> participants = this.rooms.get(roomId).getParticipants();
		for(User participant : participants) {
			participant.setVoteValue(null);
		}
	}

	public List<User> getParticipantsByRoomId(String roomId) {
		Room room = this.rooms.get(roomId);
//		Map<Integer, Integer> voteCounts = room.getVoteCounts();
//		List<User> participants = getParticipantsByRoomId(id);
//		if(room.getStatus().equals("voting") || room.getStatus().equals("results")) {
//			if(voteCounts != null) {
//				
//			}
//		}
		return room.getParticipants();
	}

	public List<ChatMessage> logChatMessage(String roomId, ChatMessage msg) {
		Room room = this.rooms.get(roomId);
		room.getChat().add(msg);
		return room.getChat();
	}

	public void resetRoom(String roomId) {
		Room room = this.rooms.get(roomId);
//		room.setChat(new ArrayList());
//		room.setCards(cards);
		room.setStatus("default");
		room.setVotingBox(null);
		room.setVoteCounts(null);
	}

	public Room updateCards(String roomId, List<Card> newCards) {
		Room room = this.rooms.get(roomId);
		room.setCards(newCards);
		return room;
	}

}
