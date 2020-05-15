package org.aashish.pointme.entity.v2;

public enum TopicState {
	
	IN_DISCUSSION("IN_DISCUSSION"),
	IN_VOTING("IN_VOTING"),
	IN_RESULTS("IN_RESULTS"),
	IS_FINISHED("IS_FINISHED");
	
	private String state;
	
	TopicState(String state) {
		this.state = state;
	}
	
	public String getState() {
		return state;
	}

}
