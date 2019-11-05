/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dxc.bankia.traffic_enrichment_rules;


import com.dxc.bankia.BaseTest;
import com.dxc.bankia.model.Country;
import com.dxc.bankia.model.Driver;
import com.dxc.bankia.model.Event;
import com.dxc.bankia.model.Vehicle;
import com.dxc.bankia.services.FinderService;
import com.dxc.bankia.services.FinderServiceImpl;
import com.dxc.bankia.services.OutputChannelImpl;
import com.dxc.bankia.util.DriverBuilder;
import com.dxc.bankia.util.EventBuilder;
import com.dxc.bankia.util.VehicleBuilder;
import org.drools.compiler.compiler.DrlParser;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.command.Command;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.*;
import org.kie.internal.utils.KieHelper;

import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.*;

import static com.dxc.bankia.model.functions.DateUtils.toDate;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


/**
 *
 * @author esteban
 */
public class RuleEventEnrichmentLoadTest {

    /**
     * Tests customer-classification-simple.drt template using the configuration
     * present in kmodule.xml.
     */


    public static void main(String[] args)
    {


        KieServices ks = KieServices.Factory.get();

        String groupId = "com.dxc.bankia";
        String artifactId = "traffic-enrichment-rules-kjar";
        String version = "1.0.0-SNAPSHOT";

        //KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(groupId, artifactId, version);

        FinderService finderService=new FinderServiceImpl();

        OutputChannelImpl errorServiceChannel = new OutputChannelImpl();
        OutputChannelImpl filterServiceChannel = new OutputChannelImpl();

        KieContainer kContainer = ks.newKieContainer(releaseId);
        //The KieContainer is wrapped by a KieScanner.
        //Note that we are never starting the KieScanner because we want to control
        //when the upgrade process kicks in.
        KieScanner scanner = ks.newKieScanner(kContainer);
        scanner.start(30_000L);


        StatelessKieSession statelessKieSession = kContainer.newStatelessKieSession("enrichmentStatelessSession");

        statelessKieSession.setGlobal("finderService", finderService);
        statelessKieSession.registerChannel("error-channel",errorServiceChannel);
        statelessKieSession.registerChannel("filter-channel",filterServiceChannel);




        Scanner scannerUtil = new Scanner(System.in);

        while (true) {

            Event event = new Event(1L, Event.Type.REQUEST_CAR_ITV_COMPLIANCE,"XSC 66666", null);


            Command newInsertOrder = ks.getCommands().newInsert(event, "eventOut");
            Command newFireAllRules = ks.getCommands().newFireAllRules("outFired");
            List<Command> cmds = new ArrayList<Command>();
            cmds.add(newInsertOrder);
            cmds.add(newFireAllRules);
            ExecutionResults execResults = statelessKieSession.execute(ks.getCommands().newBatchExecution(cmds));

            event = (Event)execResults.getValue("eventOut");
            int fired = (Integer)execResults.getValue("outFired");

            System.out.println("FILTERFILTERFILTERFILTERFILTERFILTERFILTERFILTERFILTERFILTER");
            filterServiceChannel.getMessages().stream().forEach(s -> System.out.println(s));
            System.out.println("FILTERFILTERFILTERFILTERFILTERFILTERFILTERFILTERFILTERFILTER");
            System.out.println("ERORORORORORORORORORORORORORORORORO");
            errorServiceChannel.getMessages().stream().forEach(s -> System.out.println(s));
            System.out.println("ERORORORORORORORORORORORORORORORORO");
            System.out.println("Press enter in order to run the test again....");

            filterServiceChannel.getMessages().clear();
            errorServiceChannel.getMessages().clear();
            scannerUtil.nextLine();
        }


    }






}
