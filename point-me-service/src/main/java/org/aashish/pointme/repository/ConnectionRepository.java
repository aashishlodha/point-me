package org.aashish.pointme.repository;

import java.util.List;

import org.aashish.pointme.entity.v2.Connection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConnectionRepository extends JpaRepository<Connection, String>{
	
	public List<Connection> findAllByParticipantId(String participantId);
}
