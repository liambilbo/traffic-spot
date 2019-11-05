/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dxc.bankia.kafka.service;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.stereotype.Service;

/**
 *
 * @author Dennis Federico
 */
@Service
public class TrafficEventConsumerService {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TrafficEventConsumerService.class);

    @Autowired
    private KafkaListenerEndpointRegistry registry;

    public void startService() {
        LOGGER.info("Start consumer service");
        this.registry.getListenerContainer("multi").start();
    }

    public void stopService() {
        LOGGER.info("Stop consumer service");
        this.registry.getListenerContainer("multi").stop();
    }

}
