package org.aashish.pointme.service;

import javax.transaction.Transactional;

import org.aashish.pointme.repository.ChatRepository;
import org.aashish.pointme.repository.ParticipantRepository;
import org.aashish.pointme.repository.RoomRepository;
import org.aashish.pointme.repository.TopicRepository;
import org.aashish.pointme.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class RoomCleanupService {
	
	@Autowired
	private RoomRepository roomRepo;
	
	@Autowired
	private ParticipantRepository participantRepo;
	
	@Autowired
	private ChatRepository chatRepo;
	
	@Autowired
	private VoteRepository voteRepo;
	
	@Autowired
	private TopicRepository topicRepo;
	
	@Scheduled(fixedDelayString = "PT5M")
	@Transactional
	public void cleanRooms() {
		System.out.println("Cleaning");
		String[] allRoomIds = roomRepo.getAllRoomIds();
		for (String roomId: allRoomIds) {
			int count = participantRepo.getCountOfParticipants(roomId);
			System.out.println("total: " + count);
			if(count == 0) {
				String[] topics = topicRepo.getAllRoomTopics(roomId);
				
				for(String topicId: topics) {
					chatRepo.deleteByTopicId(topicId);
					voteRepo.deleteByTopicId(topicId);
					topicRepo.deleteById(topicId);
				}
				roomRepo.deleteById(roomId);
			}
		}
	}
}
