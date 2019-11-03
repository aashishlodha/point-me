package org.aashish.pointme.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aashish.pointme.PointMeConstants;
import org.aashish.pointme.dto.Card;
import org.aashish.pointme.dto.ChatMessage;
import org.aashish.pointme.dto.Room;
import org.aashish.pointme.dto.SessionRoom;
import org.aashish.pointme.dto.User;
import org.aashish.pointme.dto.Vote;
import org.aashish.pointme.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class RoomController {
	
	@Autowired
	private RoomService roomService;
	
//	private Map<String, UserRoom> sessions = new HashMap();
	private Map<Long, List<SessionRoom>> sessions = new HashMap();
	
	private Map<String, Long> sessionRoomDictionary = new HashMap();
	
	@Autowired
    private SimpMessageSendingOperations messagingTemplate;
	
	private ObjectMapper mapper = new ObjectMapper();
	
//	@SubscribeMapping("/rooms")
	@GetMapping("/rooms")
	public @ResponseBody List<Room> findAllRooms() {
		return this.roomService.findAllRooms();
	}
	
	@GetMapping("/rooms/{id}/chat")
	public @ResponseBody List<ChatMessage> getChatMessagesByRoom(@PathVariable Integer id) {
		return this.roomService.findRoomById(id).getChat();
	}
	
	@SubscribeMapping("/rooms/{id}")
	public Room findRoomById(@DestinationVariable Integer id) {
		return this.roomService.findRoomById(id);
	}
	
	@SubscribeMapping("/rooms/{id}/participants")
	public List<User> getRoomParticipants(@DestinationVariable Integer id) {
		return this.roomService.getParticipantsByRoomId(id);
	}
	
	@SubscribeMapping("/topic/rooms/{id}/event")
	public String getRoomStatus(@DestinationVariable Integer id) {
		return this.roomService.findRoomById(id).getStatus();
	}
	
	@GetMapping("/topic/rooms/{id}/results")
	public Map getVoteResults(@PathVariable Integer id) {
		return this.roomService.findRoomById(id).getVoteCounts();
	}
	
	@MessageMapping("/rooms")
	@SendTo("/topic/rooms/created")
	public Room createRoom() {
		return roomService.createRoom();
	}
	
	@MessageMapping("/rooms/{id}/participant/update")
	public void userUserInRoom(@DestinationVariable Integer id, @Payload String userString,
			  @Header("simpSessionId") String sessionId) throws JsonParseException, JsonMappingException, IOException {
		User user = mapper.readValue(userString, User.class);
		System.out.println("User name updating..." + user);
		List<User> participants = roomService.updateParticipant(id, user);
		messagingTemplate.convertAndSend("/topic/rooms/" + id + "/participants", participants);
	}
	
	@MessageMapping("/rooms/{id}/join")
	public void joinRoom(@DestinationVariable Integer id, @Payload String userString,
			  @Header("simpSessionId") String sessionId) throws JsonParseException, JsonMappingException, IOException {
		User user1 = mapper.readValue(userString, User.class);
		System.out.println("User joining..." + user1);
//		this.sessions.put(sessionId, new UserRoom(id, user1));
		System.out.println(sessionId);
		this.sessionRoomDictionary.put(sessionId, user1.getId());
		if( this.sessions.containsKey(user1.getId()) ) {
			this.sessions.get(user1.getId()).add(new SessionRoom(sessionId, id, user1));
		} else {
			List<SessionRoom> list = new ArrayList();
			list.add(new SessionRoom(sessionId, id, user1));
			this.sessions.put(user1.getId(), list);
		}
		roomService.joinRoom(id, user1);
		Room room = this.roomService.findRoomById(id);
		messagingTemplate.convertAndSend("/topic/rooms/" + id, room);
		messagingTemplate.convertAndSend("/topic/rooms/" + id + "/participants", room.getParticipants());
		if(room.getStatus().equals(PointMeConstants.RESULTS_ROOM_STATUS)) {			
			Map voteCounts = this.findRoomById(id).getVoteCounts();
			messagingTemplate.convertAndSend("/topic/rooms/" + id + "/results", voteCounts);
		}
	}
	
	@MessageMapping("/rooms/{id}/start-voting")
	public void startVote(@DestinationVariable Integer id) {
		this.roomService.createVotingBox(id);
		this.roomService.findRoomById(id).setStatus(PointMeConstants.VOTING_ROOM_STATUS);
		messagingTemplate.convertAndSend("/topic/rooms/" + id + "/event", PointMeConstants.VOTING_ROOM_STATUS);
	}
	
	@MessageMapping("/rooms/{id}/show-results")
	public void stopVote(@DestinationVariable Integer id) {
		this.roomService.findRoomById(id).setStatus(PointMeConstants.RESULTS_ROOM_STATUS);
		messagingTemplate.convertAndSend("/topic/rooms/" + id + "/event", PointMeConstants.RESULTS_ROOM_STATUS);
		Map<Integer, Integer> results = this.roomService.countVotes(id);
		messagingTemplate.convertAndSend("/topic/rooms/" + id + "/results", results);
	}
	
	@MessageMapping("/rooms/{id}/reset")
	public void goToDefaultState(@DestinationVariable Integer id) {
		this.roomService.resetRoom(id);
		messagingTemplate.convertAndSend("/topic/rooms/" + id + "/event", "default");
		this.roomService.clearParticipantVotingByRoom(id);
		messagingTemplate.convertAndSend("/topic/rooms/" + id + "/participants", this.findRoomById(id).getParticipants());
	}
	
	@MessageMapping("/rooms/{id}/cast-vote")
	public void castVote(@DestinationVariable Integer id, @Payload String payload) throws JsonParseException, JsonMappingException, IOException {
		Vote vote = mapper.readValue(payload, Vote.class);
		this.roomService.castVote(id, vote);
		System.out.println(vote.getUser().getName() + " voted for " + vote.getValue());
		messagingTemplate.convertAndSend("/topic/rooms/" + id + "/participants", this.getRoomParticipants(id));
	}
	
	@MessageMapping("/rooms/{id}/chat")
	public void handleChatMessage(@DestinationVariable Integer id, @Payload String payload) throws JsonParseException, JsonMappingException, IOException {
		ChatMessage msg = mapper.readValue(payload, ChatMessage.class);
		System.out.println(msg.getMessage());
		List<ChatMessage> chat = this.roomService.logChatMessage(id, msg);
		messagingTemplate.convertAndSend("/topic/rooms/" + id + "/chat", chat);
	}
	
	@MessageMapping("/rooms/{id}/cards")
	public void handleRoomCardsUpdate(@DestinationVariable Integer id, @Payload String payload) throws JsonMappingException, JsonProcessingException {
		List<Card> newCards = mapper.readValue(payload, new TypeReference<List<Card>>(){});
		Room room = this.roomService.updateCards(id, newCards);
		messagingTemplate.convertAndSend("/topic/rooms/" + id, room);
	}
	
	@MessageMapping("/rooms/{id}/disconnect")
	public void disconnectRoom(@DestinationVariable Integer id, String userString, @Header("simpSessionId") String sessionId) throws JsonParseException, JsonMappingException, IOException {
		System.out.println("User disconnecting...");
		User user = mapper.readValue(userString, User.class);
		roomService.disconnectRoom(id, user);
		this.removeBySession(sessionId);
		messagingTemplate.convertAndSend("/topic/rooms/" + id, this.roomService.findRoomById(id));
		messagingTemplate.convertAndSend("/topic/rooms/" + id + "/participants", this.roomService.findRoomById(id).getParticipants());
	}
	
	private void removeBySession(String sessionId) {
		Long userId = this.sessionRoomDictionary.get(sessionId);
		List<SessionRoom> sessionList = this.sessions.get(userId);
		List<SessionRoom> newList = new ArrayList();
		for(SessionRoom sessionRoom : sessionList) {
			if(!sessionRoom.getSessionId().equals(sessionId)) {
				newList.add(sessionRoom);
			}
		}
		this.sessions.put(userId, newList);
		this.sessionRoomDictionary.remove(sessionId);
	}
	
	@MessageExceptionHandler
	@SendToUser("/topic/error")
	public String handleException(Exception e) {
		return "The request post was not found";
	}
	
	@EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        System.out.println("Received a new web socket connection");
    }
	
	@EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) throws IOException {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        System.out.println(event.getSessionId());
//        Integer roomNo = this.sessionRoomDictionary.get(event.getSessionId());
//        UserRoom userRoom = this.sessions.get(event.getSessionId());
        Long userId = this.sessionRoomDictionary.get(event.getSessionId());
        
        List<SessionRoom> sessionRoomList = this.sessions.get(userId);
        SessionRoom record = null;
        if(sessionRoomList != null && sessionRoomList.size() > 0) {
	        for(SessionRoom sessionRoom: sessionRoomList) {
	        	if(sessionRoom.getSessionId().equals(event.getSessionId())) {
	        		record = sessionRoom;
	        		break;
	        	}
	        }
	        
	       String userString = mapper.writeValueAsString(record.getUser());
	        
	        this.disconnectRoom(record.getRoomNo(), userString, event.getSessionId());
	        
	//        this.messagingTemplate.convertAndSend("/topic/rooms/" + roomNo, this.roomService.findRoomById(roomId));
	//        String username = (String) headerAccessor.getSessionAttributes().get("username");
	        System.out.println("Deleting a new web socket connection");
        }
    }
	
}
