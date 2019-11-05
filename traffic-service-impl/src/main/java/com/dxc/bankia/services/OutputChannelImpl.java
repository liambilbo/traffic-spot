package com.dxc.bankia.services;

import org.kie.api.runtime.Channel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OutputChannelImpl implements Serializable, Channel {

    List<String> messages=new ArrayList<>();

    @Override
    public void send(Object o) {
        messages.add((String )o);
    }

    public List<String> getMessages() {
        return messages;
    }

    public boolean hasMessages() { return messages.size()>0; }

}
