package org.aashish.pointme.controller;

import java.util.ArrayList;
import java.util.List;

import org.aashish.pointme.entity.v2.Message;
import org.aashish.pointme.entity.v2.Room;
import org.aashish.pointme.entity.v2.Topic;
import org.aashish.pointme.entity.v2.Vote;
import org.aashish.pointme.service.ChatService;
import org.aashish.pointme.service.RoomV2Service;
import org.aashish.pointme.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v2")
public class TopicController {
	
	@Autowired
	private TopicService topicService;
	
	@Autowired
	private RoomV2Service roomService;
	
	@Autowired
	private ChatService chatService;
	
	@GetMapping("/rooms/{roomId}/topic")
    public Topic getTopicByRoom(@PathVariable String roomId) {
		Room room = roomService.findRoomById(roomId);
    	if(room != null) {
    		topicService.findLatestTopicInRoom(room);
    	}
    	return null;
    }
	
	@GetMapping("/topics/{topicId}/chat")
	public List<Message> getChatMessagesOnTopic(@PathVariable String topicId) {
		Topic topic = topicService.findTopicById(topicId);
		if(topic != null) {
			return chatService.findAllChatMessage(topicId);
		}
		return new ArrayList<>();
	}
	
	@GetMapping("/topics/{topicId}")
	public Topic getTopicById(@PathVariable String topicId) {
		return topicService.findTopicById(topicId);
	}
	
	@GetMapping("/topics/{topicId}/votes/{participantId}")
	public String getCastedVote(@PathVariable String topicId, @PathVariable String participantId) {
		Topic topic = topicService.findTopicById(topicId);
		if(topic != null) {
			Vote castedVote = this.topicService.getCastedVote(topicId, participantId);
			if(castedVote != null) {				
				return castedVote.getVoteValue();
			}
		}
		return null;
	}
	
    @PostMapping("/rooms/{roomId}/topic")
    public Topic newTopic(@PathVariable String roomId) {
    	Room room = roomService.findRoomById(roomId);
    	if(room != null) {
    		return topicService.createNewTopic(room);
    	}
    	return null;
	}
	
	@PutMapping("/topics/{topicId}")
	public Topic updateTopic(@PathVariable String topicId, @RequestBody Topic topicUpdated) {
		Topic topic = topicService.findTopicById(topicId);
		if(topic != null) {
			topic = topicService.updateTopic(topicUpdated, topic);
		}
		return topic;
	}
	
	@PostMapping("/topics/{topicId}/vote")
	public Vote castVote(@PathVariable String topicId, @RequestBody Vote vote) {
		Topic topic = topicService.findTopicById(topicId);
		if(topic != null) {
			return topicService.castVote(topic, vote);
		}
		return null;
	}

}
