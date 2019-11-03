package org.aashish.pointme.dto;

import java.io.Serializable;

public class RoomDesciption implements Serializable{
	
	private static final long serialVersionUID = 5214867167457097573L;
	private String roomName;
	private String roomDesc = "Pointing Room";
	public String getRoomName() {
		return roomName;
	}
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	public String getRoomDesc() {
		return roomDesc;
	}
	public void setRoomDesc(String roomDesc) {
		this.roomDesc = roomDesc;
	}
	
}
