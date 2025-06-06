package com.omar.mylearnapp.controller;

import com.omar.mylearnapp.dto.TopicDTO;
import com.omar.mylearnapp.model.Topic;
import com.omar.mylearnapp.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/topics")
public class TopicController {
//TOPIC CONTR
    @Autowired
    private TopicService topicService;

    @PostMapping
    public ResponseEntity<Topic> createTopic(@RequestBody Topic topic){
        if(topicService.existsByName(topic.getName())){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        Topic createdTopic = topicService.createTopic(topic);
        return new ResponseEntity<>(createdTopic, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TopicDTO>> getAllTopics() {
        List<Topic> topics=topicService.getAllTopics();
        List<TopicDTO> dtos = topics.stream()
                .map(TopicDTO::fromTopic)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTopicById(@PathVariable Long id) {
        Optional<Topic> topic = topicService.getTopicById(id);
        return topic.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body((Topic) Map.of("error", "Topic not found")));
    }


    @PostMapping("/bulk")
    public ResponseEntity<List<Topic>> createMultipleTopics(@RequestBody List<Topic> topics){
        List<Topic> existingTopics = topics.stream()
                .filter(topic -> topicService.existsByName(topic.getName()))
                .collect(Collectors.toList());

        // If there are existing topics with the same name, return a Conflict status
        if (!existingTopics.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(existingTopics);
        }

        List<Topic> createdTopics = topicService.createMultipleTopics(topics);
        return new ResponseEntity<>(createdTopics, HttpStatus.CREATED);
    }


}
