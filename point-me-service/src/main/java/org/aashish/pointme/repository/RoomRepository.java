package org.aashish.pointme.repository;

import org.aashish.pointme.entity.v2.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoomRepository extends JpaRepository<Room, String> {

	@Query("select distinct r.id from Room r")
	public String[] getAllRoomIds();

}