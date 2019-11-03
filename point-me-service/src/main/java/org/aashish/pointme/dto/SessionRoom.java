package org.aashish.pointme.dto;

public class SessionRoom {
	
	private String sessionId;
	private Integer roomNo;
	private User user;
	
	public SessionRoom() {}

	public SessionRoom(String sessionId, Integer roomNo, User user) {
		this.sessionId = sessionId;
		this.roomNo = roomNo;
		this.user = user;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Integer getRoomNo() {
		return roomNo;
	}

	public void setRoomNo(Integer roomNo) {
		this.roomNo = roomNo;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
}
