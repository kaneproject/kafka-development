package com.curso.kafka.streams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;

public class TopicCreator {

	public static void createTopics (String ... topics) {
		Map<String,Object> config = new HashMap<>();
		config.put("bootstrap.servers","localhost:9092");
		AdminClient adminClient = AdminClient.create(config);
		List<NewTopic> topicObj = new ArrayList<>();
		for(String topic: topics) {
			topicObj.add(new NewTopic(topic,3,Short.parseShort("1")));
		}
		adminClient.createTopics(topicObj);
		adminClient.close();
	}
}
