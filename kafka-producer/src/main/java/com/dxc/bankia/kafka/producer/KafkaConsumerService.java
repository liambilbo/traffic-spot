/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dxc.bankia.kafka.producer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.TopicPartitionInitialOffset;
import org.springframework.stereotype.Component;

/**
 *
 * @author Dennis Federico
 */
@Component
public class KafkaConsumerService {

    @Autowired
    KafkaServiceConfiguration configuration;

    @Autowired
    ConsumerFactory<String, String> consumerFactory;
    
    @Autowired
    private KafkaListenerEndpointRegistry registry;

    MessageListener<String, String> messageListener = new MessageListener<String, String>() {
        @Override
        public void onMessage(ConsumerRecord<String, String> record) {
            System.out.printf("Received Message from topic:'%s' - '%s'%n", record.topic(), record.value());
        }
    };

    public KafkaMessageListenerContainer<String, String> createKafkaMessageListenerContainer(KafkaServiceConfiguration configuration, ConsumerFactory<String, String> consumerFactory) {
        ContainerProperties containerProperties = new ContainerProperties(configuration.kafkaConsumerTopic);
        containerProperties.setMessageListener(messageListener);
        KafkaMessageListenerContainer<String, String> container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        container.setBeanName("containerBean");
        return container;
    }

    private KafkaMessageListenerContainer<String, String> container;
    public void startConsuming() {
        this.registry.getListenerContainer("multi").start();
//        if (container == null) {
//            container = createKafkaMessageListenerContainer(configuration, consumerFactory);
//        }
//        container.start();
    }

    public void stopConsuming() {
        this.registry.getListenerContainer("multi").stop();
        //container.stop();
    }
        
    
    
//    @KafkaListener(id = "single", topics = "${kafka.consumer.topic:DEFAULT}", groupId = "${kafka.consumer.group:ConsumerGroup}")
//    public void listener(ConsumerRecord<String, String> record) {
//        System.out.println("Received Message in group HC " + record.value());
//    }
}
