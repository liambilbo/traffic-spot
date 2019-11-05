/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dxc.bankia.kafka.service;


import java.util.stream.IntStream;
import java.util.stream.LongStream;

import com.dxc.bankia.kafka.ApplicationConfiguration;
import com.dxc.bankia.kafka.RandomTrafficEventGenerator;
import com.dxc.bankia.kafka.producer.KafkaProducerService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.SuccessCallback;

/**
 *
 * @author Dennis Federico
 */
@Service
public class TrafficEventProducerService {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(KafkaProducerService.class);

    @Autowired
    private ApplicationConfiguration kafkaServiceConfiguration;
    
    @Autowired
    private KafkaProducerService kafkaProducer;
   
    private SuccessCallback<SendResult<String, String>> successCallback = (SendResult<String, String> result) -> {
        LOGGER.info(String.format("Sent message='%s' to Topic '%s' with offset='%d'", result.getProducerRecord().value(), result.getRecordMetadata().topic(), result.getRecordMetadata().offset()));
    };

    private FailureCallback failureCallback = (Throwable ex) -> {
        LOGGER.debug(String.format("Unable to send message due to '%s'", ex.getMessage()));
    };

    public void startService() throws InterruptedException {
        LOGGER.info("Starting CategorizationEventGenerator Service...");
        //SEND MESSAGES
        LongStream.rangeClosed(1, kafkaServiceConfiguration.produceCount)
                .mapToObj(i -> RandomTrafficEventGenerator.getRandomEvent(i))
                .forEach(e -> kafkaProducer.sendMessage(kafkaServiceConfiguration.kafkaProducerTopic, e));
        //.forEach(m -> kafkaProducer.sendMessage(kafkaServiceConfiguration.kafkaProducerTopic, m, successCallback, failureCallback));
    }
}
