package org.aashish.pointme.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aashish.pointme.entity.v2.Topic;
import org.aashish.pointme.entity.v2.Vote;
import org.aashish.pointme.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VoteService {
	
	@Autowired
	private VoteRepository voteRepo;

	public Map<String, List<String>> getTopicVoteResults(Topic topic) {
		if(topic.getState().equals("IN_RESULTS")) {
			Map<String, List<String>> result = new HashMap<String, List<String>>();
			List<Vote> voteList = voteRepo.findByTopicId(topic.getId());
			
			for(Vote vote: voteList) {
				if(!result.containsKey(vote.getVoteValue())) {
					result.put(vote.getVoteValue(), new ArrayList<String>());
				}
				result.get(vote.getVoteValue()).add(vote.getCasterName());
			}
			return result;
		}
		return null;
	}

}
