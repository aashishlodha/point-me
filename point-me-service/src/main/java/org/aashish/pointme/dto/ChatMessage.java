package org.aashish.pointme.dto;

public class ChatMessage {
	
	private String message;
	private User user;
	
	public ChatMessage() { }
	
	public ChatMessage(String message, User user) {
		this.message = message;
		this.user = user;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
}
