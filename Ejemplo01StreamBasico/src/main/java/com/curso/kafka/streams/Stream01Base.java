package com.curso.kafka.streams;

import java.util.Properties;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;

public class Stream01Base {
	
	public static final String TOPIC_ORIGEN = "stream-topic-origen";
	public static final String TOPIC_DESTINO = "stream-topic-destino";
	
	public static void main(String[] args) {
		Properties properties = new Properties();
		properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "Stream01Base");
		properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		
		TopicCreator.createTopics(TOPIC_ORIGEN,TOPIC_DESTINO);
		
		Serde<String> serdeString = Serdes.String();
		
		StreamsBuilder builder = new StreamsBuilder();
		
		KStream<String, String> kStream = builder.stream(TOPIC_ORIGEN, Consumed.with(serdeString, serdeString));
		
		KStream<String, String> kStreamUpperCased = kStream.mapValues((value)-> value.toUpperCase());
		
		kStreamUpperCased.to(TOPIC_DESTINO,Produced.with(serdeString,serdeString));
		kStreamUpperCased.print(Printed.<String, String>toSysOut().withLabel(TOPIC_DESTINO));
		Topology topology = builder.build();
		
		KafkaStreams streams = new KafkaStreams(topology, properties);
		
		Thread thread = new Thread(streams::close);
		Runtime.getRuntime().addShutdownHook(thread);
		
		streams.start();
		
	}

}
