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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

//@Controller
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
	public @ResponseBody Map<String, Room> findAllRooms() {
		return this.roomService.findAllRooms();
	}
	
	@GetMapping("/rooms/{roomId}/chat")
	public @ResponseBody List<ChatMessage> getChatMessagesByRoom(@PathVariable String roomId) {
		if(this.roomService.findRoomById(roomId) != null) {
			return this.roomService.findRoomById(roomId).getChat();
		}
		return null;
	}
	
	@SubscribeMapping("/rooms/{roomId}")
	public Room findRoomById(@DestinationVariable String roomId) {
		return this.roomService.findRoomById(roomId);
	}
	
	@SubscribeMapping("/rooms/{roomId}/participants")
	public List<User> getRoomParticipants(@DestinationVariable String roomId) {
		return this.roomService.getParticipantsByRoomId(roomId);
	}
	
	@SubscribeMapping("/topic/rooms/{roomId}/event")
	public String getRoomStatus(@DestinationVariable String roomId) {
		return this.roomService.findRoomById(roomId).getStatus();
	}
	
	@GetMapping("/topic/rooms/{roomId}/results")
	public Map<Integer, Integer> getVoteResults(@PathVariable String roomId) {
		return this.roomService.findRoomById(roomId).getVoteCounts();
	}
	
//	@MessageMapping("/rooms")
//	@SendTo("/topic/rooms/created")
	@PostMapping("/rooms")
	public @ResponseBody String createRoom() {
		return roomService.createRoom().getRoomId();
	}
	
	@MessageMapping("/rooms/{roomId}/participant/update")
	public void userUserInRoom(@DestinationVariable String roomId, @Payload String userString,
			  @Header("simpSessionId") String sessionId) throws JsonParseException, JsonMappingException, IOException {
		User user = mapper.readValue(userString, User.class);
		System.out.println("User name updating..." + user);
		List<User> participants = roomService.updateParticipant(roomId, user);
		messagingTemplate.convertAndSend("/topic/rooms/" + roomId + "/participants", participants);
	}
	
	@MessageMapping("/rooms/{roomId}/join")
	public void joinRoom(@DestinationVariable String roomId, @Payload String userString,
			  @Header("simpSessionId") String sessionId) throws IOException {
		User user1 = mapper.readValue(userString, User.class);
		System.out.println("User joining..." + user1);
//		this.sessions.put(sessionId, new UserRoom(id, user1));
		System.out.println(sessionId);
		this.sessionRoomDictionary.put(sessionId, user1.getId());
		if( this.sessions.containsKey(user1.getId()) ) {
			this.sessions.get(user1.getId()).add(new SessionRoom(sessionId, roomId, user1));
		} else {
			List<SessionRoom> list = new ArrayList();
			list.add(new SessionRoom(sessionId, roomId, user1));
			this.sessions.put(user1.getId(), list);
		}
		roomService.joinRoom(roomId, user1);
		Room room = this.roomService.findRoomById(roomId);
		messagingTemplate.convertAndSend("/topic/rooms/" + roomId, room);
		messagingTemplate.convertAndSend("/topic/rooms/" + roomId + "/participants", room.getParticipants());
		if(room.getStatus().equals(PointMeConstants.RESULTS_ROOM_STATUS)) {			
			Map voteCounts = this.findRoomById(roomId).getVoteCounts();
			messagingTemplate.convertAndSend("/topic/rooms/" + roomId + "/results", voteCounts);
		}
	}
	
	@MessageMapping("/rooms/{roomId}/start-voting")
	public void startVote(@DestinationVariable String roomId) {
		this.roomService.createVotingBox(roomId);
		this.roomService.findRoomById(roomId).setStatus(PointMeConstants.VOTING_ROOM_STATUS);
		messagingTemplate.convertAndSend("/topic/rooms/" + roomId + "/event", PointMeConstants.VOTING_ROOM_STATUS);
	}
	
	@MessageMapping("/rooms/{roomId}/show-results")
	public void stopVote(@DestinationVariable String roomId) {
		this.roomService.findRoomById(roomId).setStatus(PointMeConstants.RESULTS_ROOM_STATUS);
		messagingTemplate.convertAndSend("/topic/rooms/" + roomId + "/event", PointMeConstants.RESULTS_ROOM_STATUS);
		Map<Integer, Integer> results = this.roomService.countVotes(roomId);
		messagingTemplate.convertAndSend("/topic/rooms/" + roomId + "/results", results);
	}
	
	@MessageMapping("/rooms/{roomId}/reset")
	public void goToDefaultState(@DestinationVariable String roomId) {
		this.roomService.resetRoom(roomId);
		messagingTemplate.convertAndSend("/topic/rooms/" + roomId + "/event", "default");
		this.roomService.clearParticipantVotingByRoom(roomId);
		messagingTemplate.convertAndSend("/topic/rooms/" + roomId + "/participants", this.findRoomById(roomId).getParticipants());
	}
	
	@MessageMapping("/rooms/{roomId}/cast-vote")
	public void castVote(@DestinationVariable String roomId, @Payload String payload) throws JsonParseException, JsonMappingException, IOException {
		Vote vote = mapper.readValue(payload, Vote.class);
		this.roomService.castVote(roomId, vote);
		System.out.println(vote.getUser().getName() + " voted for " + vote.getValue());
		messagingTemplate.convertAndSend("/topic/rooms/" + roomId + "/participants", this.getRoomParticipants(roomId));
	}
	
	@MessageMapping("/rooms/{roomId}/chat")
	public void handleChatMessage(@DestinationVariable String roomId, @Payload String payload) throws JsonParseException, JsonMappingException, IOException {
		ChatMessage msg = mapper.readValue(payload, ChatMessage.class);
		System.out.println(msg.getMessage());
		List<ChatMessage> chat = this.roomService.logChatMessage(roomId, msg);
		messagingTemplate.convertAndSend("/topic/rooms/" + roomId + "/chat", chat);
	}
	
	@MessageMapping("/rooms/{roomId}/cards")
	public void handleRoomCardsUpdate(@DestinationVariable String roomId, @Payload String payload) throws JsonProcessingException {
		List<Card> newCards = mapper.readValue(payload, new TypeReference<List<Card>>(){});
		Room room = this.roomService.updateCards(roomId, newCards);
		messagingTemplate.convertAndSend("/topic/rooms/" + roomId, room);
	}
	
	@MessageMapping("/rooms/{roomId}/disconnect")
	public void disconnectRoom(@DestinationVariable String roomId, String userString, @Header("simpSessionId") String sessionId) throws IOException {
		System.out.println("User disconnecting...");
		User user = mapper.readValue(userString, User.class);
		roomService.disconnectRoom(roomId, user);
		this.removeBySession(sessionId);
		messagingTemplate.convertAndSend("/topic/rooms/" + roomId, this.roomService.findRoomById(roomId));
		messagingTemplate.convertAndSend("/topic/rooms/" + roomId + "/participants", this.roomService.findRoomById(roomId).getParticipants());
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
	        
	        this.disconnectRoom(record.getRoomId(), userString, event.getSessionId());
	        
	//        this.messagingTemplate.convertAndSend("/topic/rooms/" + roomNo, this.roomService.findRoomById(roomId));
	//        String username = (String) headerAccessor.getSessionAttributes().get("username");
	        System.out.println("Deleting a new web socket connection");
        }
    }
	
}
