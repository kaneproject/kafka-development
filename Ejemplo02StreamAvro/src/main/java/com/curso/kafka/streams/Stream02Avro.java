package com.curso.kafka.streams;

import java.util.Collections;
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

import com.curso.kafka.avro.model.Clima;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;

public class Stream02Avro {

	public static final String TOPIC_ORIGEN = "stream-avro-topic-origen";
	public static final String TOPIC_DESTINO = "stream-avro-topic-destino";

	public static void main(String[] args) {
		Properties properties = new Properties();
		properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "Stream01Base");
		properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class.getName());
		properties.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");

		TopicCreator.createTopics(TOPIC_ORIGEN, TOPIC_DESTINO);

		Serde<String> serdeString = Serdes.String();
		SpecificAvroSerde<Clima> climaSerde = new SpecificAvroSerde<>();
		climaSerde.configure(Collections.singletonMap(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
				"http://localhost:8081"), false);

		StreamsBuilder builder = new StreamsBuilder();

		KStream<String, Clima> kStream = builder.stream(TOPIC_ORIGEN, Consumed.with(serdeString, climaSerde));

		KStream<String, Clima> kStreamUpperCased = kStream.mapValues((value) -> {
			value.setNombre((value.getNombre() + "").toUpperCase());
			return value;
		});

		kStreamUpperCased.to(TOPIC_DESTINO, Produced.with(serdeString, climaSerde));
		kStreamUpperCased.print(Printed.<String, Clima>toSysOut().withLabel(TOPIC_DESTINO));
		Topology topology = builder.build();

		KafkaStreams streams = new KafkaStreams(topology, properties);

		Thread thread = new Thread(streams::close);
		Runtime.getRuntime().addShutdownHook(thread);

		streams.start();

	}

}
