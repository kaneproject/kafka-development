package com.curso.kafka.streams;

import java.io.IOException;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import com.curso.kafka.avro.model.Clima;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;

public class ProductorClima {
	public static String CITY = "madrid";
	
	public static void main(String[] args) throws IOException, InterruptedException {
		Properties properties = new Properties();
		properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
		properties.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");
		
		
		KafkaProducer<String, Clima> kafkaProducer = new KafkaProducer<>(properties);
		
		Thread thread = new Thread(kafkaProducer::close);
		Runtime.getRuntime().addShutdownHook(thread);
		
		while(true) {
			Clima clima = OpenWeatherMap.getWeatherFromOpenWeatherMap(CITY);
	
			ProducerRecord<String, Clima> record = new ProducerRecord<>(Stream02Avro.TOPIC_ORIGEN, CITY, clima);
			kafkaProducer.send(record);
			Thread.sleep(1500);
		}
	}
}
