package org.aashish.pointme.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.aashish.pointme.dto.ChatMessage;
import org.aashish.pointme.dto.Room;
import org.aashish.pointme.dto.User;
import org.aashish.pointme.dto.Vote;
import org.springframework.stereotype.Service;

@Service
public class RoomService {
	
	private List<Room> rooms = new LinkedList<>();

	public RoomService() {
		this.rooms.add(new Room(1));
		this.rooms.add(new Room(2));
		this.rooms.add(new Room(3));
		this.rooms.add(new Room(4));
		this.rooms.add(new Room(5));
		this.rooms.add(new Room(6));
		this.rooms.add(new Room(7));
		this.rooms.add(new Room(8));
		this.rooms.add(new Room(9));
		this.rooms.add(new Room(10));
		this.rooms.add(new Room(11));
	}
	
	public List<Room> findAllRooms() {
		return this.rooms;
	}
	
	public Room findRoomById(int id) {
		return this.rooms.get(id - 1);
	}
	
	public Room createRoom() {
		Room room = new Room(this.rooms.size() + 1);
		this.rooms.add(room);
		return room;
	}
	
	public void joinRoom(Integer roomNo, User user) {
		Room room = rooms.get(roomNo - 1);
		if(room.getRoomOwner() == null) {
			room.setRoomOwner(user);
		}
		if(room.getStatus().equals("voting") || room.getStatus().equals("results")) {
			user.setVoteValue(room.getVotingBox().get(user.getId()));
		}
		for(User participant : this.rooms.get(roomNo-1).getParticipants()) {
			if(participant.getId().equals(user.getId())) {
				participant.setVoteValue(user.getVoteValue());
				return;
			}
		}
		this.rooms.get(roomNo-1).getParticipants().add(user);
	}
	
	public void disconnectRoom(Integer roomNo, User user) {
		Room room = this.rooms.get(roomNo-1);
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
			this.rooms.set(roomNo - 1, new Room(roomNo));
		}
	}
	
	public List<User> updateParticipant(Integer id, User user) {
		List<User> participants = this.rooms.get(id - 1).getParticipants();
		for(User participant : participants) {
			if(participant.getId().equals(user.getId())) {
				participant.setName(user.getName());
				break;
			}
		}
		return participants;
	}

	public void createVotingBox(Integer id) {
		Map<Long, Integer> votingBox = new HashMap();
		this.rooms.get(id - 1).setVotingBox(votingBox);
	}

	public void castVote(Integer id, Vote vote) {
		User user = vote.getUser();
		List<User> participants = this.rooms.get(id - 1).getParticipants();
		for(User participant : participants) {
			if(user.getId().equals(participant.getId())) {
				participant.setVoteValue(vote.getValue());
				break;
			}
		}
		this.rooms.get(id - 1).getVotingBox().put(user.getId(), vote.getValue());
	}

	public Map<Integer, Integer> countVotes(Integer id) {
		Map<Long, Integer> votingBox = this.rooms.get(id - 1).getVotingBox();
		Map<Integer, Integer> results = new HashMap();
		for(Entry entry : votingBox.entrySet()) {
			if(results.containsKey(entry.getValue())) {
				results.put(Integer.valueOf(entry.getValue().toString()), results.get(entry.getKey()) + 1);
			} else {
				results.put(Integer.parseInt(entry.getValue().toString()), 1);
			}
		}
		
		this.rooms.get(id - 1).setVoteCounts(results);
		return results;
	}

	public void clearParticipantVotingByRoom(Integer id) {
		List<User> participants = this.rooms.get(id - 1).getParticipants();
		for(User participant : participants) {
			participant.setVoteValue(null);
		}
	}

	public List<User> getParticipantsByRoomId(Integer id) {
		Room room = this.rooms.get(id - 1);
//		Map<Integer, Integer> voteCounts = room.getVoteCounts();
//		List<User> participants = getParticipantsByRoomId(id);
//		if(room.getStatus().equals("voting") || room.getStatus().equals("results")) {
//			if(voteCounts != null) {
//				
//			}
//		}
		return room.getParticipants();
	}

	public List<ChatMessage> logChatMessage(Integer id, ChatMessage msg) {
		Room room = this.rooms.get(id - 1);
		room.getChat().add(msg);
		return room.getChat();
	}

	public void resetRoom(Integer id) {
		Room room = this.rooms.get(id - 1);
//		room.setChat(new ArrayList());
//		room.setCards(cards);
		room.setStatus("default");
		room.setVotingBox(null);
		room.setVoteCounts(null);
	}

}
