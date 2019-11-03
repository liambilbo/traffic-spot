package com.dxc.bankia.services;

import com.dxc.bankia.model.Notification;
import org.kie.api.runtime.Channel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NotificationChannelImpl implements Serializable, Channel {

    List<Notification> notifications=new ArrayList<>();

    @Override
    public void send(Object o) {
        notifications.add((Notification ) o);
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public boolean hasNotifications() { return notifications.size()>0; }

}
