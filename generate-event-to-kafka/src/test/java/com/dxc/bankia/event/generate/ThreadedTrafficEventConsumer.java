package com.dxc.bankia.event.generate;

import com.dxc.bankia.model.Event;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This example expects a topic "test-topic" to exist with 2 partitions
 */
public class ThreadedTrafficEventConsumer {

    private volatile boolean doneConsuming = false;
    private int numberPartitions;
    private ExecutorService executorService;

    public ThreadedTrafficEventConsumer(int numberPartitions) {
        this.numberPartitions = numberPartitions;
    }


    public void startConsuming() {
        executorService = Executors.newFixedThreadPool(numberPartitions);
        Properties properties = getConsumerProps();

        for (int i = 0; i < numberPartitions; i++) {
            Runnable consumerThread = getConsumerThread(properties);
            executorService.submit(consumerThread);
        }
    }

    private Runnable getConsumerThread(Properties properties) {
        return () -> {
            Consumer<Long, Event> consumer = null;
            try {
                consumer = new KafkaConsumer<>(properties);
                consumer.subscribe(Collections.singletonList("traffic-events"));
                while (!doneConsuming) {
                    ConsumerRecords<Long, Event> records = consumer.poll(5000);
                    for (ConsumerRecord<Long, Event> record : records) {
                        String message = String.format("Consumed: key = %s value = %s with offset = %d partition = %d",
                                record.key(), record.value(), record.offset(), record.partition());
                        System.out.println(message);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (consumer != null) {
                    consumer.close();
                }
            }
        };
    }

    public void stopConsuming() throws InterruptedException {
        doneConsuming = true;
        executorService.awaitTermination(10000, TimeUnit.MILLISECONDS);
        executorService.shutdownNow();
    }


    private Properties getConsumerProps() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("group.id", "simple-consumer-example");
        properties.put("auto.offset.reset", "earliest");
        properties.put("enable.auto.commit", "true");
        properties.put("auto.commit.interval.ms", "3000");
        properties.put("key.deserializer", LongDeserializer.class);
        properties.put("value.deserializer", EventDeserializer.class);

        return properties;

    }

    /**
     * Change the constructor arg to match the actual number of partitions
     */

    public static void main(String[] args) throws InterruptedException {
        ThreadedTrafficEventConsumer consumerExample = new ThreadedTrafficEventConsumer(2);
        consumerExample.startConsuming();
        Thread.sleep(60000); //Run for one minute
        consumerExample.stopConsuming();
    }

}
