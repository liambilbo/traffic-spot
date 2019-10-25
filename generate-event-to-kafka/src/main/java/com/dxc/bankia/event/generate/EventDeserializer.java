package com.dxc.bankia.event.generate;

import java.util.Map;

import com.dxc.bankia.model.Event;
import org.apache.kafka.common.serialization.Deserializer;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;


public class EventDeserializer implements Deserializer<Event> {

    @Override

    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override

    public Event deserialize(String topic, byte[] data) {

        ObjectMapper mapper = new ObjectMapper();

        Event object = null;

        try {

            object = mapper.readValue(data, Event.class);

        } catch (Exception exception) {

            System.out.println("Error in deserializing bytes "+ exception);

        }

        return object;

    }

    @Override

    public void close() {

    }

}