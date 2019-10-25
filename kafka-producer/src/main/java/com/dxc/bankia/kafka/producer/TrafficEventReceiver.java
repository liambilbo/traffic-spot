package com.dxc.bankia.kafka.producer;

import java.util.concurrent.CountDownLatch;

import com.dxc.bankia.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;


public class TrafficEventReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrafficEventReceiver.class);

    private CountDownLatch latch = new CountDownLatch(1);

    public CountDownLatch getLatch() {
        return latch;
    }

    @KafkaListener(topics = "${kakfa.topic.json}")
    public void receive(Event car) {
        LOGGER.info("received car='{}'", car.toString());
        latch.countDown();
    }
}
