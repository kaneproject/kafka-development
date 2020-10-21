package com.curso.kafka;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class ManualConsumer {
    public static String KAFKA_HOST = "localhost:9092";
    private static final AtomicBoolean closed = new AtomicBoolean(false);

    public static void main(String[] args) throws InterruptedException {
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                System.out.println("Shutting down");
                closed.set(true);
            }
        });

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_HOST);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "manual-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(SimpleProducer.TOPIC));
/* partition =  1   offset =   404   key =  key[0]   value =   message[0]
partition =  0   offset =   384   key =  key[1]   value =   message[1]
partition =  0   offset =   385   key =  key[4]   value =   message[4]
partition =  1   offset =   405   key =  key[3]   value =   message[3]
partition =  2   offset =   373   key =  key[2]   value =   message[2]
partition =  2   offset =   374   key =  key[5]   value =   message[5]
partition =  0   offset =   386   key =  key[7]   value =   message[7]
 */
        while (!closed.get()) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("partition = %2d   offset = %5d   key = %7s   value = %12s\n",
                        record.partition(), record.offset(), record.key(), record.value());
                Thread.sleep(5000);
            }

            consumer.commitSync();
        }

        consumer.close();
    }
}