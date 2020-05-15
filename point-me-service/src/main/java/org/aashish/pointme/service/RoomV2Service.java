package org.aashish.pointme.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.aashish.pointme.entity.v2.Participant;
import org.aashish.pointme.entity.v2.Room;
import org.aashish.pointme.entity.v2.Topic;
import org.aashish.pointme.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomV2Service {
	
	@Autowired
	private RoomRepository roomRepo;
	
	@Autowired
	private TopicService topicService;
	
	public Room createRoom(Participant owner) {
		Room room = new Room();
        room.setId(UUID.randomUUID().toString());
        room.setOwnerId(owner.getId());
        room.setOwnerName(owner.getName());
        room = roomRepo.save(room);
        Topic topic = topicService.createNewTopic(room);
        room.setCurrentTopicId(topic.getId());
        room = roomRepo.save(room);
        return room;
	}
	
	public Room findRoomById(String roomId) {
		Optional<Room> room = this.roomRepo.findById(roomId);
    	if(room.isPresent()) {
    		return room.get();
    	}
    	return null;
	}
	
	public List<Room> findAllRooms() {
		return roomRepo.findAll();
	}
	
	public Room updateLatestTopic(Room room, String topicId) {
		room.setCurrentTopicId(topicId);
		return roomRepo.save(room);
	}

}
