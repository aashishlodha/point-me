package org.aashish.pointme.repository;

import java.util.List;
import java.util.Optional;

import org.aashish.pointme.entity.v2.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, String> {

	public Optional<Vote> findByCasterId(String casterId);

	public Vote findByCasterIdAndTopicId(String casterId, String topicId);
	
	public List<Vote> findByTopicId(String TopicId);

	public void deleteByTopicId(String topicId);
	
}
