package com.dxc.bankia.event.objects;

import com.dxc.bankia.model.Event;
import com.dxc.bankia.model.Notification;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EventExecuted implements Serializable {
    private Event event;
    private int result;

    private boolean withErrors=false;
    private boolean withNotifications=false;
    private boolean filtered=false;


    private boolean enriched=false;
    private List<String> errors=new ArrayList<>();
    private List<String> filters=new ArrayList<>();

    private List<Notification> notifications=new ArrayList<>();

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public List<String> getFilters() {
        return errors;
    }

    public void setFilters(List<String> errors) {
        this.filters = filters;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public boolean isWithErrors() {
        return withErrors;
    }

    public void setWithErrors(boolean withErrors) {
        this.withErrors = withErrors;
    }

    public boolean isFiltered() {
        return filtered;
    }

    public void setFiltered(boolean filtered) {
        this.filtered = filtered;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public EventExecuted(Event event, int result) {
        this.event = event;
        this.result = result;
    }

    public boolean isEnriched() {
        return enriched;
    }

    public void setEnriched(boolean enriched) {
        this.enriched = enriched;
    }

    public void addError(String message){
        this.errors.add(message);
    }

    public void addFiltered(String message){
        this.filters.add(message);
    }

    public void addNotification(Notification notification){
        this.notifications.add(notification);
    }

    public boolean isWithNotifications() {
        return withNotifications;
    }

    public void setWithNotifications(boolean withNotifications) {
        this.withNotifications = withNotifications;
    }


    @Override
    public String toString() {
        return "EventExecuted{" +
                "event=" + event.getId() +
                 " -type " + event.getType() +
                 " -IdentificationNumber " +  event.getIdentificationNumber() +
                 " -registrationNumber " +  event.getRegistrationNumber() +
                ", result=" + result +
                ", withErrors=" + withErrors +
                ", withNotifications=" + withNotifications +
                ", filtered=" + filtered +
                ", enriched=" + enriched +
                '}';
    }
}
