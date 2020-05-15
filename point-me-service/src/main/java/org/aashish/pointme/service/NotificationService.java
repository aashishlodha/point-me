package org.aashish.pointme.service;

import org.aashish.pointme.entity.v2.Message;
import org.aashish.pointme.entity.v2.Participant;
import org.aashish.pointme.entity.v2.ResourceAction;
import org.aashish.pointme.entity.v2.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
	
	@Autowired
    private SimpMessageSendingOperations messagingTemplate;
	
	public void notifyRoomParticipants(String roomId, ResourceAction<Participant> partAction) {
		messagingTemplate.convertAndSend("/topic/rooms/" + roomId + "/participants", partAction);
	}
	
	public void notifyTopicChanged(String roomId, Topic topic) {
		messagingTemplate.convertAndSend("/topic/rooms/" + roomId + "/topic", topic);
	}
	
	public void notifyTopicUpdates(String topicId, Topic topic) {
		messagingTemplate.convertAndSend("/topic/topics/" + topicId, topic);
	}

	public void pushChatMessage(String topicId, Message message) {
		messagingTemplate.convertAndSend("/topic/topics/" + topicId + "/chat", message);
	}
}
