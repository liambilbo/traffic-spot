/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dxc.bankia.kafka.service;

import com.dxc.bankia.kafka.ApplicationConfiguration;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Service;

/**
 *
 * @author Dennis Federico
 */
@Service
public class KafkaConsumerService {

    private KafkaMessageListenerContainer<String, String> container;

    private MessageListener<String, String> messageListener = new MessageListener<String, String>() {
        @Override
        public void onMessage(ConsumerRecord<String, String> record) {
            System.out.printf("Received Message from topic:'%s' - '%s'%n", record.topic(), record.value());
        }
    };

    @Autowired
    public KafkaConsumerService(ApplicationConfiguration configuration, ConsumerFactory<String, String> consumerFactory) {
        container = createKafkaMessageListenerContainer(configuration, consumerFactory);
    }

    private KafkaMessageListenerContainer<String, String> createKafkaMessageListenerContainer(ApplicationConfiguration configuration, ConsumerFactory<String, String> consumerFactory) {
        ContainerProperties containerProperties = new ContainerProperties(configuration.kafkaConsumerTopic);
        containerProperties.setMessageListener(messageListener);
        containerProperties.setGroupId("SomeOtherGroup");
        KafkaMessageListenerContainer<String, String> container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        container.setBeanName("containerBean");
        return container;
    }

    public void startConsuming() {
        container.start();
    }

    public void stopConsuming() {
        container.stop();
    }

//    @KafkaListener(id = "single", topics = "${kafka.consumer.topic:DEFAULT}", groupId = "${kafka.consumer.group:ConsumerGroup}")
//    public void listener(ConsumerRecord<String, String> record) {
//        System.out.println("Received Message in group HC " + record.value());
//    }
}
