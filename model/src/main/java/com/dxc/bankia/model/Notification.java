package com.dxc.bankia.model;

import java.io.Serializable;
import java.util.Objects;

public class Notification implements Serializable {

    public enum Type {
        NOTICE,WARNING,NOT_COMPLIANCE,SANCTION
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Event.Type getEventType() {
        return eventType;
    }

    public void setEventType(Event.Type eventType) {
        this.eventType = eventType;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return id.equals(that.id) &&
                eventId.equals(that.eventId) &&
                eventType == that.eventType &&
                type == that.type &&
                Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, eventId, eventType, type, message);
    }

    private Long id;
    private Long eventId;
    private Event.Type eventType;
    private Type type;
    private String message;

    public Notification(){

    }


}
