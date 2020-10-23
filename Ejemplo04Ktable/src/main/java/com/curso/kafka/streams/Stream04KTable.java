package com.curso.kafka.streams;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.TimeWindowedKStream;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;

import com.curso.kafka.avro.model.Clima;
import com.curso.kafka.avro.model.Datos;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;

public class Stream04KTable {

	public static final String TOPIC_ORIGEN = "stream-topology-topic-origen";
	public static final String TOPIC_DATOS = "stream-topology-topic-datos";
	public static final String TOPIC_DESTINO = "stream-topology-topic-destino";

	public static void main(String[] args) {
		Properties properties = new Properties();
		properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "Stream03Topology");
		properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		
		TopicCreator.createTopics(TOPIC_ORIGEN, TOPIC_DESTINO);

				StreamsBuilder builder = new StreamsBuilder();

		KStream<String, String> kStream = builder.stream(TOPIC_ORIGEN);

		KGroupedStream<String, String> kGroupedStream = kStream.map((operacion,usuario)->
			new KeyValue<>(usuario, usuario))
			.groupByKey();
		TimeWindowedKStream<String, String> windowedBy = 
				kGroupedStream.windowedBy(TimeWindows.of(Duration.ofSeconds(20)));
		KTable<Windowed<String>, Long> kTable = windowedBy.count();
		KTable<Windowed<String>, Long> kTableFiltrado = kTable.filter((usuario,count)-> count > 200);
		kTableFiltrado
		
		
		Topology topology = builder.build();

		KafkaStreams streams = new KafkaStreams(topology, properties);

		Thread thread = new Thread(streams::close);
		Runtime.getRuntime().addShutdownHook(thread);

		streams.start();

	}

}
