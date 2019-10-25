/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dxc.bankia.kafka.producer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import com.dxc.bankia.model.Event;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.util.concurrent.SuccessCallback;

/**
 *
 * @author Dennis Federico
 */
@Service
public class TrafficEventGeneratorService {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TrafficEventGeneratorService.class);

    @Autowired
    private KafkaServiceConfiguration kafkaServiceConfiguration;

    @Autowired
    private KafkaProducerService kafkaProducer;
    
    @Autowired
    private KafkaConsumerService kafkaConsumerService;
    
    private SuccessCallback<SendResult<String, String>> successCallback = (SendResult<String, String> result) -> {
        LOGGER.info(String.format("Sent message='%s' to Topic '%s' with offset='%d'", result.getProducerRecord().value(), result.getRecordMetadata().topic(), result.getRecordMetadata().offset()));
    };
    
    private FailureCallback failureCallback = (Throwable ex) -> {
        LOGGER.debug(String.format("Unable to send message due to '%s'", ex.getMessage()));
    };

    public void startService() throws InterruptedException {
        LOGGER.info("Starting CategorizationEventGenerator Service...");
        LOGGER.info("Publishing to topic:" + kafkaServiceConfiguration.kafkaProducerTopic);
        LOGGER.info("Publishing to topic:" + kafkaServiceConfiguration.kafkaBootstrapHost);
        LOGGER.info("Publishing to topic:" + kafkaServiceConfiguration.kafkaBootstrapPort);


        Event event1 = new Event();
        event1.setId(1L);
        event1.setType(Event.Type.REQUEST_CAR_ITV_COMPLIANCE);
        event1.setRegistrationNumber("XSC 1234");

        Event event2 = new Event();
        event1.setId(2L);
        event1.setType(Event.Type.REQUEST_CAR_ITV_COMPLIANCE);
        event1.setRegistrationNumber("XSC 66666");



        Event event3 = new Event();
        event1.setId(3L);
        event1.setType(Event.Type.REQUEST_DRIVER_ITV_COMPLIANCE);
        event1.setRegistrationNumber("A3456737X");

        Event event4 = new Event();
        event1.setId(4L);
        event1.setType(Event.Type.REQUEST_DRIVER_ITV_COMPLIANCE);
        event1.setRegistrationNumber("VD345737X");


        List<Event> events=new ArrayList();
        events.add(event1);
        events.add(event2);
        events.add(event3);
        events.add(event4);

        //SEND MESSAGES

        Random randomGenerator = new Random();
        LongStream.rangeClosed(1, kafkaServiceConfiguration.produceCount).mapToObj(i -> {Event e=events.get(randomGenerator.nextInt(events.size()-1)); e.setId(i);return e;})
                .forEach(m -> kafkaProducer.sendMessage(kafkaServiceConfiguration.kafkaProducerTopic, m));
                //.forEach(m -> kafkaProducer.sendMessage(kafkaServiceConfiguration.kafkaProducerTopic, m, successCallback, failureCallback));
    
        //RECEIVE MESSAGES - WE CAN START THE CONTAINER ANY TIME, BUT BETTER AT THE END TO AVOID MESSAGE OVERLAP
        kafkaConsumerService.startConsuming();
        Thread.sleep(5000L); // wait a bit for the container to start
        kafkaConsumerService.stopConsuming();    
    }
}
