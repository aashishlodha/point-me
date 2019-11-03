package org.aashish.pointme.dto;

public class UserRoom {
	
	private Integer roomNo;
	private User user;
	
	public UserRoom() {}
	
	public UserRoom(Integer roomNo, User user) {
		this.roomNo = roomNo;
		this.user = user;
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
	public void setUserId(User user) {
		this.user = user;
	}

}
