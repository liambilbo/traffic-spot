package com.dxc.bankia.event.generate;

import com.dxc.bankia.model.Event;
import com.dxc.bankia.util.EventBuilder;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.LongSerializer;

import java.util.*;
import java.util.concurrent.Future;

/**
 * Example of a simple producer, not meant to run as a stand alone example.
 *
 * If desired to run this example change the ProducerRecord below to
 * use a real topic name and comment out line #33 below.
 */
public class TrafficEventProducer {



    public static void main(String[] args) {

        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("key.serializer", LongSerializer.class);
        properties.put("value.serializer", EventSerializer.class);
        properties.put("acks", "1");
        properties.put("retries", "3");
        properties.put("compression.type", "snappy");

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




                try(Producer<Long, Event> producer = new KafkaProducer<>(properties)) {


                    for (long index = 0; index < 1000; index++) {

                        Event eventTmp=events.get(randomGenerator.nextInt(events.size()));

                        Event event=new EventBuilder()
                                .withId(index)
                                .withType(eventTmp.getType())
                                .withIdentificationNumber(eventTmp.getIdentificationNumber())
                                .withRegistrationNumber(eventTmp.getRegistrationNumber())
                                .build();


                    ProducerRecord<Long, Event> record = new ProducerRecord<Long, Event>("traffic-events", event.getId(),event);
                    Callback callback = (metadata, exception) -> {
                        if (exception != null) {
                            exception.printStackTrace();
                        } else {
                            System.out.println("Record sent with key " + event.getId() + " to partition " + metadata.partition()

                                    + " with offset " + metadata.offset());
                        }
                    };

                    Future<RecordMetadata> sendFuture = producer.send(record, callback);

                }

            }


    }


}
