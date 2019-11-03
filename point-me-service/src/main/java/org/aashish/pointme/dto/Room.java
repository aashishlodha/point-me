package org.aashish.pointme.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.aashish.pointme.PointMeConstants;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Room implements Serializable{
	
	private static final long serialVersionUID = 5564146684586467617L;
	private Integer roomNo;
	private RoomDesciption roomDesc;
	private String status;
	private User roomOwner;
	
	@JsonIgnore
	private List<User> participants = new ArrayList<>();
	private List<Card> cards = new ArrayList<>();
	@JsonIgnore
	private List<ChatMessage> chat = new LinkedList<>();
	@JsonIgnore
	private Map<Long, Integer> votingBox = null;
	@JsonIgnore
	private Map<Integer, Integer> voteCounts = null;
	
	public Room() {
		setStatus(PointMeConstants.DEFAULT_ROOM_STATUS);
		cards.add(new Card(1));
		cards.add(new Card(2));
		cards.add(new Card(3));
		cards.add(new Card(4));
		cards.add(new Card(5));
	}
	
	public Room(Integer roomNo) {
		this();
		this.roomNo = roomNo;
	}

	public Integer getRoomNo() {
		return roomNo;
	}

	public void setRoomNo(Integer roomNo) {
		this.roomNo = roomNo;
	}

	public RoomDesciption getRoomDesc() {
		return roomDesc;
	}

	public void setRoomDesc(RoomDesciption roomDesc) {
		this.roomDesc = roomDesc;
	}

	public List<User> getParticipants() {
		return participants;
	}

	public void setParticipants(List<User> participants) {
		this.participants = participants;
	}

	public List<Card> getCards() {
		return cards;
	}

	public void setCards(List<Card> cards) {
		this.cards = cards;
	}

	public List<ChatMessage> getChat() {
		return chat;
	}
	
	public void setChat(List<ChatMessage> chat) {
		this.chat = chat;
	}

	public Map<Long, Integer> getVotingBox() {
		return votingBox;
	}

	public void setVotingBox(Map<Long, Integer> votingBox) {
		this.votingBox = votingBox;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Map<Integer, Integer> getVoteCounts() {
		return voteCounts;
	}

	public void setVoteCounts(Map<Integer, Integer> voteCounts) {
		this.voteCounts = voteCounts;
	}

	public User getRoomOwner() {
		return roomOwner;
	}

	public void setRoomOwner(User roomOwner) {
		this.roomOwner = roomOwner;
	}

}
