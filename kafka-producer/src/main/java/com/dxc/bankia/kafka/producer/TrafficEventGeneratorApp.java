/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dxc.bankia.kafka.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *
 * @author Dennis Federico
 */
@SpringBootApplication
public class TrafficEventGeneratorApp implements CommandLineRunner {

    @Autowired
    TrafficEventGeneratorService generatorService;
    
    public static void main(String[] args) {
        SpringApplication.run(TrafficEventGeneratorApp.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        generatorService.startService();
    }
}
