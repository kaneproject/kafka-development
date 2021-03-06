package com.curso.kafka.productorclima;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import com.curso.kafka.avro.model.Clima;

public class ConsumidorClima {

	public static void main(String[] args) throws IOException {
		Properties properties = new Properties();
		properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
		properties.put(ConsumerConfig.GROUP_ID_CONFIG, "ConsumidorClima");
		
		KafkaConsumer<String, byte[]> kafkaConsumer = new KafkaConsumer<>(properties);
		Thread thread = new Thread(kafkaConsumer::close);
		Runtime.getRuntime().addShutdownHook(thread);
		
		kafkaConsumer.subscribe(Collections.singletonList(ProductorClima.TOPIC));
		while(true) {
			ConsumerRecords<String, byte[]> records = kafkaConsumer.poll(Duration.ofMillis(100));
			for(ConsumerRecord<String, byte[]> record: records) {
				Clima clima = deserializeClima(record.value());
				System.out.println("ID: " +record.key() + "VALUE: " + clima.toString());
			}
		}
	}

	private static Clima deserializeClima(byte[] value) throws IOException {
		
		return Clima.fromByteBuffer(ByteBuffer.wrap(value));
	}

}
