package com.dxc.bankia.event.generate;

import com.dxc.bankia.model.Event;
import com.dxc.bankia.util.EventBuilder;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class EventGenerationToKafka {

    private static final Pattern SPACE = Pattern.compile(" ");

    private static Map<String, Object> buildKafkaParams(String brokers) {
        Map<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put("bootstrap.servers", brokers); //localhost:9092,anotherhost:9092
        kafkaParams.put("key.deserializer", LongSerializer.class);
        kafkaParams.put("value.deserializer", EventSerializer.class);
        kafkaParams.put("key.serializer", LongSerializer.class);
        kafkaParams.put("value.serializer", EventSerializer.class);
        //kafkaParams.put("key.serializer", StringSerializer.class);
        //kafkaParams.put("value.serializer", StringSerializer.class);
        kafkaParams.put("group.id", "processorGroup");
        kafkaParams.put("auto.offset.reset", "latest");
        kafkaParams.put("enable.auto.commit", false);
        return kafkaParams;
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: EventGenerationToKafka <brokers> <topics>\n" +
                    "  <brokers> is a list of one or more Kafka brokers\n" +
                    "  <topics> is a list of one or more kafka topics to consume from\n\n");
            System.exit(1);
        }

        //Programmatically set the driver log level (only for stand-alone?)
        //org.apache.log4j.Logger.getRootLogger().setLevel(Level.WARN);

        //Prepare Kafka Config
        Map<String, Object> kafkaParams = buildKafkaParams(args[0]);
        Set<String> topics = new HashSet<>(Arrays.asList(args[1].split(",")));
        String topic=topics.iterator().next();
        Producer<Long, Event> producer = createProducer(kafkaParams);


        Event event1 = new EventBuilder()
                .withId(1L)
                .withType(Event.Type.REQUEST_CAR_ITV_COMPLIANCE)
                .withRegistrationNumber("XSC 1234")
                .build();

        Event event2 = new EventBuilder()
                .withId(2L)
                .withType(Event.Type.REQUEST_CAR_ITV_COMPLIANCE)
                .withRegistrationNumber("XSC 66666")
                .build();

        Event event3 = new EventBuilder()
                .withId(3L)
                .withType(Event.Type.REQUEST_DRIVER_ITV_COMPLIANCE)
                .withIdentificationNumber("A3456737X")
                .build();

        Event event4 = new EventBuilder()
                .withId(4L)
                .withType(Event.Type.REQUEST_DRIVER_ITV_COMPLIANCE)
                .withIdentificationNumber("VD345737X")
                .build();


        List<Event> events=new ArrayList();
        events.add(event1);
        events.add(event2);
        events.add(event3);
        events.add(event4);

        Random randomGenerator = new Random();
        for (long index = 0; index < 1000; index++) {

            Event eventTmp=events.get(randomGenerator.nextInt(events.size()));

            Event event=new EventBuilder()
                    .withId(index)
                    .withType(eventTmp.getType())
                    .withIdentificationNumber(eventTmp.getIdentificationNumber())
                    .withRegistrationNumber(eventTmp.getRegistrationNumber())
                    .build();


           ProducerRecord<Long, Event> record = new ProducerRecord<Long, Event>(topic, event.getId(),event);


            try {

                RecordMetadata metadata = producer.send(record).get();

                System.out.println("Record sent with key " + index + " to partition " + metadata.partition()

                        + " with offset " + metadata.offset());

            } catch (ExecutionException e) {

                System.out.println("Error in sending record");

                System.out.println(e);

            } catch (InterruptedException e) {

                System.out.println("Error in sending record");

                System.out.println(e);

            }

        }

    }


    static Producer<Long, Event> createProducer(Map<String, Object> kafkaparams) {
        Properties props = new Properties();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaparams.get("bootstrap.servers"));

        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaparams.get("key.serializer"));

        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaparams.get("value.serializer"));

        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, kafkaparams.get("enable.auto.commit"));

        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaparams.get("auto.offset.reset"));


        return new KafkaProducer<Long,Event>(props);

   }

    static Producer<Long, Event> createConsumer(Map<String, Object> kafkaparams) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaparams.get("bootstrap.servers"));
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaparams.get("key.serializer"));

        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaparams.get("value.serializer"));

        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, kafkaparams.get("enable.auto.commit"));

        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaparams.get("auto.offset.reset"));


        return new KafkaProducer<Long,Event>(props);

    }


}
