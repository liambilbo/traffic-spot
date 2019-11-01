/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dxc.bankia.event.serializer;

import com.dxc.bankia.model.Event;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;
import java.util.Map;

/**
 *
 */
public class EventDeserializer implements Deserializer<Event> {

    static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public Event deserialize(String topic, byte[] data) {
        if (data != null) {
            try {
                Event result = objectMapper.readValue(data, Event.class);
                return result;
            } catch (IOException e) {
                e.printStackTrace(System.err);
                return null;
            }
        }
        return null;
    }

    @Override
    public void close() {
    }
}
