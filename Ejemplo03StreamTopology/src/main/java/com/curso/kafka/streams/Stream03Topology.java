package com.curso.kafka.streams;

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
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;

import com.curso.kafka.avro.model.Clima;
import com.curso.kafka.avro.model.Datos;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;

public class Stream03Topology {

	public static final String TOPIC_ORIGEN = "stream-topology-topic-origen";
	public static final String TOPIC_DATOS = "stream-topology-topic-datos";
	public static final String TOPIC_DESTINO = "stream-topology-topic-destino";

	public static void main(String[] args) {
		Properties properties = new Properties();
		properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "Stream03Topology");
		properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class.getName());
		properties.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");

		TopicCreator.createTopics(TOPIC_ORIGEN, TOPIC_DESTINO);

		Serde<String> serdeString = Serdes.String();
		SpecificAvroSerde<Clima> climaSerde = new SpecificAvroSerde<>();
		climaSerde.configure(Collections.singletonMap(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
				"http://localhost:8081"), false);
		SpecificAvroSerde<Datos> datosSerde = new SpecificAvroSerde<>();
		datosSerde.configure(Collections.singletonMap(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
				"http://localhost:8081"), false);

		StreamsBuilder builder = new StreamsBuilder();

		KStream<String, Clima> kStream = builder.stream(TOPIC_ORIGEN, Consumed.with(serdeString, climaSerde));

		KStream<String, Clima> kStreamUpperCased = kStream.mapValues((value) -> {
			value.setNombre((value.getNombre() + "").toUpperCase());
			return value;
		});
		KStream<String, Datos> kStreamDatos = kStreamUpperCased
			.<String,Datos>map(
					(key,value)-> new KeyValue<String, Datos>(value.getNombre().toString(),value.getDatos())
					);
		kStreamUpperCased.to(TOPIC_DESTINO, Produced.with(serdeString, climaSerde));
		kStreamUpperCased.print(Printed.<String, Clima>toSysOut().withLabel(TOPIC_DESTINO));
		kStreamDatos.to(TOPIC_DATOS,Produced.with(serdeString, datosSerde));
		kStreamDatos.print(Printed.<String,Datos>toSysOut().withLabel(TOPIC_DATOS));
		KTable<String, Datos> table = kStreamDatos.toTable();
		
		Topology topology = builder.build();

		KafkaStreams streams = new KafkaStreams(topology, properties);

		Thread thread = new Thread(streams::close);
		Runtime.getRuntime().addShutdownHook(thread);

		streams.start();

	}

}
