package org.aashish.pointme.entity.v2;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="votes")
@Getter
@Setter
@NoArgsConstructor
public class Vote extends AuditModel{
	
	@Id
	private String id;
	private String casterId;
	private String casterName;
	private String voteValue;
	
	@ManyToOne
	@JoinColumn(name="topic_id")
	private Topic topic;

}
