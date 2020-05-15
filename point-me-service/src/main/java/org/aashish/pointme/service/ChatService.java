package org.aashish.pointme.service;

import java.util.List;
import java.util.UUID;

import org.aashish.pointme.entity.v2.Message;
import org.aashish.pointme.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

	@Autowired
	private ChatRepository chatRepo;
	
	@Autowired
	private NotificationService notificationService;
	
	public List<Message> findAllChatMessage(String topicId) {
		return chatRepo.findAllChatMessageByTopicIdOrderByTime(topicId);
	}

	public void pushChatMessage(String topicId, Message message) {
		message.setId(UUID.randomUUID().toString());
		message.setTime(System.currentTimeMillis());;
		message.setTopicId(topicId);
		message = chatRepo.save(message);
		notificationService.pushChatMessage(topicId, message);
	}
	
}
