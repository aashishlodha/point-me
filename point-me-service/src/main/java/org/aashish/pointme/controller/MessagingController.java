package org.aashish.pointme.controller;

import java.util.List;

import org.aashish.pointme.entity.v2.Message;
import org.aashish.pointme.entity.v2.Participant;
import org.aashish.pointme.entity.v2.Topic;
import org.aashish.pointme.service.ChatService;
import org.aashish.pointme.service.ParticipantService;
import org.aashish.pointme.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Controller
public class MessagingController {
	
	@Autowired
	private ParticipantService participantService;
	
	@Autowired
	private TopicService topicService;
	
	@Autowired
	private ChatService chatService;
	
	@MessageMapping({"/rooms/{roomId}/topic","topic/topics/{topicId}"})
	public Topic updateTopic(@Payload Topic topicUpdated) {
		Topic topic = this.topicService.findTopicById(topicUpdated.getId());
		if(topic != null) {
			topicUpdated = topicService.updateTopic(topicUpdated, topic);
		}
		return topicUpdated;
	}
	
	@MessageMapping("/topics/{topicId}/chat")
	@SubscribeMapping("/topics/{topicId}/chat")
	public void pushMessageToTopicChat(@DestinationVariable String topicId, @Payload Message message) {
		Topic topic = this.topicService.findTopicById(topicId);
		if(topic != null) {
			chatService.pushChatMessage(topicId, message);
		}
	}
	
	@SubscribeMapping("/topics/{topicId}")
	public Topic subscribeToRoomTopic(@DestinationVariable String topicId) {
		return null;
	}
	
	@MessageMapping("/rooms/{roomId}/participants")
	@SubscribeMapping("/rooms/{roomId}/participants")
	public List<Participant> subscribeToRoomParticipants(@DestinationVariable String roomId, @Payload Participant participant,
			@Header("simpSessionId") String simpSessionId) {
		participantService.addConnection(participant.getId(), simpSessionId);
		return null;
	}
	
	/*@EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        System.out.println("Received a new web socket connection");
        String simpSessionId = event.getMessage().getHeaders().get("simpSessionId").toString();
        
    }*/
	
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        String simpSessionId = event.getSessionId();
        participantService.removeConnection(simpSessionId);
    }

}
