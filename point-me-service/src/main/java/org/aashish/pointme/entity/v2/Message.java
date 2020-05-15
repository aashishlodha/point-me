package org.aashish.pointme.entity.v2;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
public class Message {
	
	@Id
	private String id;
	private String text;
	private String byId;
	private String byName;
	private Long time;
	private String topicId;

}
