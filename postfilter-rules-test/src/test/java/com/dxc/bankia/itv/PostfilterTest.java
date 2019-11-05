/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dxc.bankia.itv;


import com.dxc.bankia.BaseTest;
import com.dxc.bankia.model.Country;
import com.dxc.bankia.model.Event;
import com.dxc.bankia.model.Vehicle;
import com.dxc.bankia.services.FinderService;
import com.dxc.bankia.services.FinderServiceImpl;
import com.dxc.bankia.services.NotificationChannelImpl;
import com.dxc.bankia.services.OutputChannelImpl;
import com.dxc.bankia.util.VehicleBuilder;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.command.Command;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.dxc.bankia.model.functions.DateUtils.toDate;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;


/**
 *
 * @author esteban
 */
public class PostfilterTest extends BaseTest {

    private String groupId = "com.dxc.bankia";
    private String  artifactId = "traffic-postfilter-rules-kjar";
    private String  version = "1.0.0-SNAPSHOT";
    private FinderService finderService=new FinderServiceImpl();
    private OutputChannelImpl errorServiceChannel = new OutputChannelImpl();
    private OutputChannelImpl filterServiceChannel = new OutputChannelImpl();
    private NotificationChannelImpl notificationServiceChannel = new NotificationChannelImpl();

    /**
     * Tests customer-classification-simple.drt template using the configuration
     * present in kmodule.xml.
     */
    @Test
    public void testSimpleTemplateWithSpreadsheet1(){

        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(groupId, artifactId, version);

        StatelessKieSession statelessKieSession = createStatelessSession("postFilterStatelessSession",releaseId);
        //FinderService finderService=new FinderServiceImpl
        //Get Blog type from kie base
        statelessKieSession.setGlobal("finderService", finderService);
        statelessKieSession.registerChannel("error-channel",errorServiceChannel);
        statelessKieSession.registerChannel("filter-channel",filterServiceChannel);
        statelessKieSession.registerChannel("notification-channel",notificationServiceChannel);

        doTest(statelessKieSession);
    }



    private void doTest(StatelessKieSession statelessKieSession){

        KieServices ks = KieServices.Factory.get();

        Event event = new Event();
        event.setId(1l);
        event.setType(Event.Type.REQUEST_CAR_ITV_COMPLIANCE);
        event.setRegistrationNumber("XSC 1234");

        Vehicle vehicle=finderService.getVehicleByRegistrationNumber("XSC 1234");

        event.setVehicle(vehicle);

        Command newInsertOrder = ks.getCommands().newInsert(event, "eventOut");
        Command newFireAllRules = ks.getCommands().newFireAllRules("outFired");
        List<Command> cmds = new ArrayList<Command>();
        cmds.add(newInsertOrder);
        cmds.add(newFireAllRules);
        ExecutionResults execResults = statelessKieSession.execute(ks.getCommands().newBatchExecution(cmds));

        event = (Event)execResults.getValue("eventOut");
        int fired = (Integer)execResults.getValue("outFired");

        if (errorServiceChannel.hasMessages()) {
            errorServiceChannel.getMessages().stream().forEach((e) -> System.out.println(e));
            errorServiceChannel.getMessages().clear();
        }

        if (filterServiceChannel.hasMessages()) {
            filterServiceChannel.getMessages().stream().forEach((e) -> System.out.println(e));
            filterServiceChannel.getMessages().clear();
         }

        if (notificationServiceChannel.hasNotifications()) {
            notificationServiceChannel.getNotifications().stream().forEach((e) -> System.out.println(e));
            notificationServiceChannel.getNotifications().clear();
          }


    }


}
