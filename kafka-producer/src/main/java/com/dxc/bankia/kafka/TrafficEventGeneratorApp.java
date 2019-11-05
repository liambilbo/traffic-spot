/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dxc.bankia.kafka;

import com.dxc.bankia.kafka.service.KafkaConsumerService;
import com.dxc.bankia.kafka.service.TrafficEventConsumerService;
import com.dxc.bankia.kafka.service.TrafficEventProducerService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *
 */
@SpringBootApplication
public class TrafficEventGeneratorApp implements CommandLineRunner {
    
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TrafficEventGeneratorApp.class);

    @Autowired
    TrafficEventProducerService producerService;

    @Autowired
    TrafficEventConsumerService consumerService;
    
    @Autowired
    KafkaConsumerService kafkaConsumerService;

    public static void main(String[] args) {
        SpringApplication.run(TrafficEventGeneratorApp.class, args);
    }

    @Override
    public void run(String... args) throws Exception {


        LOGGER.info(">>> WARMING UP!!!");
        Thread.sleep(2000L);
        LOGGER.info(">>> SENDING MESSAGES!!!");
        producerService.startService();
        LOGGER.info(">>> MESSAGES SENT!!!");


        /*
        LOGGER.info(">>> START SENDING MESSAGES!!!");
        producerService.startService();
        LOGGER.info(">>> MESSAGES SENT!!!");
        
        Thread.sleep(2000L);
        
        LOGGER.info(">>> START RECEIVING MESSAGES 1!!!");
        consumerService.startService();
        Thread.sleep(3000L);
        consumerService.stopService();
        LOGGER.info(">>> MESSAGES RECEIVED!!!");
        
        Thread.sleep(2000L);

        LOGGER.info(">>> START RECEIVING MESSAGES 1!!!");
        kafkaConsumerService.startConsuming();        
        Thread.sleep(3000L);
        kafkaConsumerService.stopConsuming();
        LOGGER.info(">>> MESSAGES RECEIVED!!!");*/
    }
}
