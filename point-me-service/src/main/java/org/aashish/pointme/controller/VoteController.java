package org.aashish.pointme.controller;

import java.util.List;
import java.util.Map;

import org.aashish.pointme.entity.v2.Topic;
import org.aashish.pointme.service.TopicService;
import org.aashish.pointme.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v2")
public class VoteController {

	@Autowired
	private VoteService voteService;
	
	@Autowired
	private TopicService topicService;
	
	@GetMapping("topics/{topicId}/results")
	public Map<String, List<String>> getVoteResults(@PathVariable String topicId) {
		Topic topic = topicService.findTopicById(topicId);
		if(topic != null) {
			return voteService.getTopicVoteResults(topic);
		}
		return null;
	}
}
