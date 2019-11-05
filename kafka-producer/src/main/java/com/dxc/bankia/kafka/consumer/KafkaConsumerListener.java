/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dxc.bankia.kafka.consumer;

import com.dxc.bankia.model.Event;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 *
 * @author Dennis Federico
 */
@Component
@KafkaListener(id = "multi", topics = "${kafka.consumer.topic:DEFAULT}", groupId = "${kafka.consumer.group:ConsumerGroup}", autoStartup = "false", containerFactory = "kafkaEventListenerContainerFactory")
public class KafkaConsumerListener {
  
    @KafkaHandler
    public void listener(@Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp, Event record) {
        System.out.printf("Received Event [%d}: %s%n", timestamp, record);
    }
}
