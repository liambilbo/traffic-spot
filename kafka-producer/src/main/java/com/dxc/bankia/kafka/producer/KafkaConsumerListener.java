/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dxc.bankia.kafka.producer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 *
 * @author Dennis Federico
 */
@Component
@KafkaListener(id = "multi", topics = "${kafka.consumer.topic:DEFAULT}", groupId = "${kafka.consumer.group:ConsumerGroup}", autoStartup = "false")
public class KafkaConsumerListener {

    @KafkaHandler
    public void listener(ConsumerRecord<String, String> record) {
        System.out.println("Received Message in group HC " + record.value());
    }

    @KafkaHandler(isDefault = true)
    public void listenDefault(Object object) {
        System.out.println("Received Message in group HC " + object.toString());
    }

}
