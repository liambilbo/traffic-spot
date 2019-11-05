package com.dxc.bankia.event.objects;

import com.dxc.bankia.model.Event;

import java.util.HashMap;
import java.util.Map;

public class EventMessage {

    private Long id;
    private Event.Type type;
    //private Timestamp timestamp;
    private String registrationNumber;
    private String identificationNumber;

    private Map<String,String> payload=new HashMap<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Event.Type getType() {
        return type;
    }

    public void setType(Event.Type type) {
        this.type = type;
    }

    /*public Timestamp getTimestamp() {
        return timestamp;
    }*/

    /*
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }*/

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public Map<String, String> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, String> payload) {
        this.payload = payload;
    }


}
