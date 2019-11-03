package org.aashish.pointme.dto;

public class User{
	
	private Long id;
	private String name;
	private Integer voteValue;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getVoteValue() {
		return voteValue;
	}
	public void setVoteValue(Integer voteValue) {
		this.voteValue = voteValue;
	}
}
