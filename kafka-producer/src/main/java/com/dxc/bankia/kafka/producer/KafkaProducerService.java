/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dxc.bankia.kafka.producer;

import com.dxc.bankia.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SuccessCallback;

/**
 *
 * @author Dennis Federico
 */
@Component
public class KafkaProducerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerService.class);

    @Autowired
    @Qualifier("stringMessageProducerKafkaTemplate")
    private KafkaTemplate<String, String> stringKafkaTemplate;
    
    @Autowired
    @Qualifier("eventProducerKafkaTemplate")
    private KafkaTemplate<String, Event> eventKafkaTemplate;

    public void sendMessage(String topic, String message) {
        LOGGER.debug(String.format("%s -> SendingMessage - To:'%s' Message:'%s'", this.getClass().getName(), topic, message));
        stringKafkaTemplate.send(topic, message);
    }
    
    public void sendMessage(String topic, Event event) {
        LOGGER.debug(String.format("%s -> SendingMessage - To:'%s' Message:'%s'", this.getClass().getName(), topic, event));
        eventKafkaTemplate.send(topic, event);
    }

    public void sendMessage(String topic, String message, SuccessCallback<SendResult<String, String>> successCallback, FailureCallback failureCallback) {
        ListenableFuture<SendResult<String, String>> future = stringKafkaTemplate.send(topic, message);
        future.addCallback(successCallback, failureCallback);
    }
}
