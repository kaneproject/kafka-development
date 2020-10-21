package com.curso.kafka;
import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

public class SeekConsumer {
    public static String KAFKA_HOST = "localhost:9092";
    private static final AtomicBoolean closed = new AtomicBoolean(false);

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
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "500");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "seek-group3");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        Set<TopicPartition> partitions = new HashSet<>();
        partitions.add(new TopicPartition(SimpleProducer.TOPIC, 0));
        partitions.add(new TopicPartition(SimpleProducer.TOPIC, 1));
        partitions.add(new TopicPartition(SimpleProducer.TOPIC, 2));
        consumer.assign(partitions);

        consumer.seekToBeginning(Collections.singleton(new TopicPartition(SimpleProducer.TOPIC, 0)));
        consumer.seekToEnd(Collections.singleton(new TopicPartition(SimpleProducer.TOPIC, 1)));

        consumer.seek(new TopicPartition(SimpleProducer.TOPIC, 2), 500);

        while (!closed.get()) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("topic = %2s partition = %2d   offset = %5d   key = %7s   value = %12s\n",
                        record.topic(), record.partition(), record.offset(), record.key(), record.value());
            }
        }

        consumer.close();
    }
}