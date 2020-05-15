package org.aashish.pointme.entity.v2;

import lombok.Data;

@Data
public class TopicUpdate {
	
	private String topicId;
	private String topicName;
	private String description;
	private String cards = "1,2,3,5,8";
	private String topicState;

}
