package com.curso.kafka.streams;

import java.io.IOException;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class ProductorClima {

	public static String CITY = "madrid";
	
	public static void main(String[] args) throws IOException, InterruptedException {
		Properties properties = new Properties();
		properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

		
		KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties);
		
		Thread thread = new Thread(kafkaProducer::close);
		Runtime.getRuntime().addShutdownHook(thread);
		int i=1;
		while(true) {
			i++;
			ProducerRecord<String, String> record = new ProducerRecord<>(Stream01Base.TOPIC_ORIGEN, CITY, "String " + i);
			kafkaProducer.send(record);
			System.out.println("Record : " + record.toString());
			Thread.sleep(1500);
		}
	}
}
