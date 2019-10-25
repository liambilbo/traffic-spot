package com.dxc.bankia.kafka.producer;

import com.dxc.bankia.model.Event;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1,
        topics = {TrafficEventKafkaApplicationTest.CAT_TOPIC})
public class TrafficEventKafkaApplicationTest {


    static final String CAT_TOPIC = "CatTopic";
    @Autowired
    private TrafficEventSender sender;

    @Autowired
    private TrafficEventReceiver receiver;

    @Test
    public void testReceive() throws Exception {
        Event event1 = new Event();
        event1.setId(1L);
        event1.setType(Event.Type.REQUEST_CAR_ITV_COMPLIANCE);
        event1.setRegistrationNumber("XSC 1234");
/*

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
 */

        sender.send(event1);




        receiver.getLatch().await(10000, TimeUnit.MILLISECONDS);
        assertThat(receiver.getLatch().getCount()).isEqualTo(0);
    }
}