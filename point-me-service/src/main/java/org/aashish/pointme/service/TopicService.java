package org.aashish.pointme.service;

import java.util.Optional;
import java.util.UUID;

import org.aashish.pointme.entity.v2.Room;
import org.aashish.pointme.entity.v2.Topic;
import org.aashish.pointme.entity.v2.Vote;
import org.aashish.pointme.repository.TopicRepository;
import org.aashish.pointme.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TopicService {
	
	@Autowired
	private TopicRepository topicRepo;
	
	@Autowired
	private RoomV2Service roomService;
	
	@Autowired
	private NotificationService notService;
	
	@Autowired
	private ParticipantService participantService;
	
	@Autowired
	private VoteRepository voteRepo;
	
	public Topic createNewTopic(Room room) {
		Topic topic = new Topic();
        topic.setName("Topic not yet added...");
        topic.setId(UUID.randomUUID().toString());
        topic.setRoom(room);
        topic.setState("IN_DISCUSSION");
        topicRepo.save(topic);
        roomService.updateLatestTopic(room, topic.getId());
        participantService.resetParticipantVotingState(room);
        return topic;
	}

	public Topic findTopicById(String topicId) {
		Optional<Topic> topic = topicRepo.findById(topicId);
		if(topic.isPresent()) {
			return topic.get();
		}
		else{
			return null;
		}
	}
	
	public Topic findLatestTopicInRoom(Room room) {
		return topicRepo.findLatestTopicInRoom(room.getId());
	}

	public Topic updateTopic(Topic topicUpdated, Topic topic) {
		boolean isFinished = false;
		String topicId = topic.getId();
		if(topicUpdated.getState().equals("IS_FINISHED") && topic.getState().equals("IN_RESULTS")) {
			isFinished = true;
		}
		topic.setName(topicUpdated.getName());
		topic.setState(topicUpdated.getState());
		topic.setCards(topicUpdated.getCards());
		topic.setDesc(topicUpdated.getDesc());
		
		topic = topicRepo.save(topic);
		
		if(isFinished) {
			topic = this.createNewTopic(topic.getRoom());
		}
		notService.notifyTopicUpdates(topicId, topic);
		return topic;
	}

	public Vote castVote(Topic topic, Vote voteCasted) {
		Optional<Vote> voteFind = voteRepo.findByCasterId(voteCasted.getCasterId());
		Vote vote = null; 
		if (voteFind.isPresent()) {
			vote = voteFind.get();
		} else {
			vote = new Vote();
			vote.setId(UUID.randomUUID().toString());
		}
		vote.setCasterId(voteCasted.getCasterId());
		vote.setCasterName(voteCasted.getCasterName());
		vote.setTopic(topic);
		vote.setVoteValue(voteCasted.getVoteValue());
		vote = voteRepo.save(vote);
		
		participantService.notifyVoted(voteCasted.getCasterId());
		
		return vote;
	}

	public Vote getCastedVote(String topicId, String participantId) {
		// TODO Auto-generated method stub
		return voteRepo.findByCasterIdAndTopicId(participantId, topicId);
	}

}
