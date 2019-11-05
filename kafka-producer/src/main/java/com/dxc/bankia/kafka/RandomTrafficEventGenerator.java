package com.dxc.bankia.kafka;

import com.dxc.bankia.model.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomTrafficEventGenerator {

    static List<Event> events = new ArrayList<>();
    static Random random = new Random();
    static {
        Event event1 = new Event();
        event1.setId(1L);
        event1.setType(Event.Type.REQUEST_CAR_ITV_COMPLIANCE);
        event1.setRegistrationNumber("XSC 1234");

        Event event2 = new Event();
        event2.setId(1L);
        event2.setType(Event.Type.REQUEST_CAR_ITV_COMPLIANCE);
        event2.setRegistrationNumber("XSC 66666");

        Event event3 = new Event();
        event3.setId(1L);
        event3.setType(Event.Type.REQUEST_DRIVER_ITV_COMPLIANCE);
        event3.setIdentificationNumber("A3456737X");

        Event event4 = new Event();
        event4.setId(1L);
        event4.setType(Event.Type.REQUEST_DRIVER_ITV_COMPLIANCE);
        event4.setIdentificationNumber("XVD345737X");

        Event event5 = new Event();
        event2.setId(1L);
        event2.setType(Event.Type.REQUEST_CAR_ITV_COMPLIANCE);
        event2.setRegistrationNumber("ES 217");

        events.add(event1);
        events.add(event2);
        events.add(event3);
        events.add(event4);
        events.add(event5);

    }

    public static Event getRandomEvent(Long id){
        Event event=new Event();
        event.setId(id);
        Event eventTmp=events.get(random.nextInt(events.size() -1));
        event.setType(eventTmp.getType());
        event.setIdentificationNumber(eventTmp.getIdentificationNumber());
        event.setRegistrationNumber(eventTmp.getRegistrationNumber());
        return event;
    }
}
