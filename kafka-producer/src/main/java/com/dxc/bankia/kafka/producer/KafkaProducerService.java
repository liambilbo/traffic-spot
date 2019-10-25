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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.util.concurrent.SuccessCallback;

/**
 *
 * @author Dennis Federico
 */
@Component
public class KafkaProducerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerService.class);

    @Autowired
    @Qualifier("producerKafkaTemplate")
    private KafkaTemplate<String, Event> kafkaTemplate;


    public void sendMessage(String topic, Event message) {
        LOGGER.debug(String.format("%s -> SendingMessage - To:'%s' Message:'%s'", this.getClass().getName(), topic, message));
        kafkaTemplate.send(topic, message);
    }

    public void sendMessage(String topic, Event message, SuccessCallback<SendResult<String, Event>> successCallback, FailureCallback failureCallback) {
        ListenableFuture<SendResult<String, Event>> future = kafkaTemplate.send(topic, message);
        future.addCallback(successCallback, failureCallback);
    }
}
