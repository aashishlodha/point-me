package org.aashish.pointme.repository;

import java.util.List;

import org.aashish.pointme.entity.v2.Room;
import org.aashish.pointme.entity.v2.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TopicRepository extends JpaRepository<Topic, String>{
	
	public List<Topic> findAllByRoom(Room room);
	
	@Query(value = "SELECT * FROM Topic t WHERE t.room.id = :roomId ORDER BY t.createdAt DESC",
            nativeQuery = true)
	public Topic findLatestTopicInRoom(String roomId);

	@Query("select t.id from Topic t where t.room.id = :roomId")
	public String[] getAllRoomTopics(String roomId);

}
