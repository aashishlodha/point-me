package org.aashish.pointme.repository;

import java.util.List;

import org.aashish.pointme.entity.v2.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Message, String> {
	List<Message> findAllChatMessageByTopicIdOrderByTime(String topidId);

	void deleteByTopicId(String topicId);
}
