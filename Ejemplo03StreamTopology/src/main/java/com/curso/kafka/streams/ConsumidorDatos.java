package com.curso.kafka.streams;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import com.curso.kafka.avro.model.Clima;
import com.curso.kafka.avro.model.Datos;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.subject.RecordNameStrategy;

public class ConsumidorDatos {

	public static void main(String[] args) throws IOException {
		Properties properties = new Properties();
		properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
		properties.put(ConsumerConfig.GROUP_ID_CONFIG, "ConsumidorDatos");
		properties.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");
		properties.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);

		
		KafkaConsumer<String, Datos> kafkaConsumer = new KafkaConsumer<>(properties);
		Thread thread = new Thread(kafkaConsumer::close);
		Runtime.getRuntime().addShutdownHook(thread);
		
		kafkaConsumer.subscribe(Collections.singletonList(Stream03Topology.TOPIC_DATOS));
		while(true) {
			ConsumerRecords<String, Datos> records = kafkaConsumer.poll(Duration.ofMillis(100));
			for(ConsumerRecord<String, Datos> record: records) {			
				System.out.println("ID: " +record.key() + "VALUE: " + record.value().toString());
			}
		}
	}
}
