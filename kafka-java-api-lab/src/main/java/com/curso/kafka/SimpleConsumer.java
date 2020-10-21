package com.curso.kafka;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

public class SimpleConsumer {
	/*
	 * partition =  2   offset =    56   key = key[202] timestamp = 1603213543599  value = message[202]
	partition =  2   offset =    57   key = key[203] timestamp = 1603213544599  value = message[203]
	partition =  0   offset =    70   key = key[204] timestamp = 1603213545601  value = message[204]
	partition =  0   offset =    71   key = key[205] timestamp = 1603213546655  value = message[205]
	partition =  1   offset =    76   key = key[206] timestamp = 1603213547673  value = message[206]
	partition =  1   offset =    77   key = key[207] timestamp = 1603213548676  value = message[207]
	*/
	public static String KAFKA_HOST = "localhost:9092";
    private static final AtomicBoolean closed = new AtomicBoolean(false);//para cerrar al matar el proceso

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                System.out.println("Shutting down");
                closed.set(true);
            }
        });

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_HOST);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "simple-consumer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(SimpleProducer.TOPIC));

        while (!closed.get()) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<String, String> record : records)
                System.out.printf("partition = %2d   offset = %5d   key = %7s timestamp = %8s  value = %12s\n",
                        record.partition(), record.offset(), record.key(), String.valueOf(record.timestamp()), record.value());
        }
        

        consumer.close();
    }
}