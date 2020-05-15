package org.aashish.pointme.repository;

import java.util.List;

import org.aashish.pointme.entity.v2.Participant;
import org.aashish.pointme.entity.v2.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ParticipantRepository extends JpaRepository<Participant, String> {
	
	public List<Participant> findAllByRoom(Room room);

	@Query("select count(p) from Participant p where p.room.id = :roomId")
	public Integer getCountOfParticipants(String roomId);

}