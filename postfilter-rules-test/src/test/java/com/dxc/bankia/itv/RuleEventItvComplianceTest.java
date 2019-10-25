/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dxc.bankia.itv;


import com.dxc.bankia.BaseTest;
import com.dxc.bankia.model.Event;
import com.dxc.bankia.model.Notification;
import com.dxc.bankia.services.FinderService;
import com.dxc.bankia.services.FinderServiceImpl;
import com.dxc.bankia.util.EventBuilder;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.event.rule.DebugRuleRuntimeEventListener;
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.KieSession;

import java.util.HashSet;
import java.util.Set;


/**
 *
 * @author esteban
 */
public class RuleEventItvComplianceTest extends BaseTest {

    private FinderService finderService = new FinderServiceImpl();

    /**
     * Tests customer-classification-simple.drt template using the configuration
     * present in kmodule.xml.
     */
    @Test
    public void testSimpleTemplateWithSpreadsheet1(){

        KieServices ks = KieServices.Factory.get();

        String groupId = "com.dxc.bankia";
        String artifactId = "traffic-postfilter-rules-kjar";
        String version = "1.4.0";

        //KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(groupId, artifactId, version);

        KieSession ksession = this.createSession("postFilterSession",releaseId);

        //ksession.setGlobal("finderService", finderService);
        //ksession.addEventListener(new DebugAgendaEventListener());
        ksession.addEventListener(new DebugRuleRuntimeEventListener());

        this.doTest(ksession);
    }



    private void doTest(KieSession ksession){

        //Implement a Channel that notifies AuditService when new instances of
        //SuspiciousOperation are available.
        final Set<Event> erroResults = new HashSet<>();
        final Set<Notification> notificationResults = new HashSet<>();
        Channel errorServiceChannel = new Channel(){

            @Override
            public void send(Object object) {
                //notify AuditService here. For testing purposes, we are just
                //going to store the received object in a Set.
                erroResults.add((Event) object);
            }

        };

        Channel notificationServiceChannel = new Channel(){

            @Override
            public void send(Object object) {
                //notify AuditService here. For testing purposes, we are just
                //going to store the received object in a Set.
                notificationResults.add((Notification) object);
            }

        };


        ksession.registerChannel("error-channel",errorServiceChannel);
        ksession.registerChannel("notification-channel",notificationServiceChannel);

        Event event1=new EventBuilder()
                .withId(1L)
                .withType(Event.Type.REQUEST_CAR_ITV_COMPLIANCE)
                .withRegistrationNumber("XSC 1234")
                .build();
        event1.setVehicle(finderService.getVehicleByRegistrationNumber("XSC 1234"));



        ksession.insert(event1);

        ksession.fireAllRules();

        event1.getVehicle();


    }


}
