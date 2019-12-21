package org.aashish.pointme.dto;

public class SessionRoom {
	
	private String sessionId;
	private String roomId;
	private User user;
	
	public SessionRoom() {}

	public SessionRoom(String sessionId, String roomId, User user) {
		this.sessionId = sessionId;
		this.roomId = roomId;
		this.user = user;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
}
