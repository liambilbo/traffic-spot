package com.dxc.bankia.kafka.producer;

import com.dxc.bankia.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

public class TrafficEventSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrafficEventSender.class);

    @Value("${kafka.topic.json}")
    private String jsonTopic;

    @Autowired
    private KafkaTemplate<String, Event> kafkaTemplate;

    public void send(Event event) {
        LOGGER.info("sending event='{}'", event.toString());
        kafkaTemplate.send(jsonTopic, event);
    }
}