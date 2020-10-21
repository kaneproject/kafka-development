package com.curso.kafka.productorclima;

import java.io.IOException;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import com.curso.kafka.avro.model.Clima;

public class ProductorClima {
	public static String TOPIC = "avro-clima";
	public static String CITY = "madrid";
	
	public static void main(String[] args) throws IOException, InterruptedException {
		Properties properties = new Properties();
		properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
		
		TopicCreator.createTopics(TOPIC);
		
		KafkaProducer<String, byte[]> kafkaProducer = new KafkaProducer<>(properties);
		
		Thread thread = new Thread(kafkaProducer::close);
		Runtime.getRuntime().addShutdownHook(thread);
		
		while(true) {
			Clima clima = OpenWeatherMap.getWeatherFromOpenWeatherMap(CITY);
			byte[] climaSerializado =serializeClima(clima);
			ProducerRecord<String, byte[]> record = new ProducerRecord<>(TOPIC, CITY, climaSerializado);
			kafkaProducer.send(record);
			Thread.sleep(1500);
		}
	}

	private static byte[] serializeClima(Clima clima) throws IOException {
	
		return clima.toByteBuffer().array();
	}
}
