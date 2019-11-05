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
import com.dxc.bankia.services.OutputChannelImpl;
import com.dxc.bankia.util.DriverBuilder;
import com.dxc.bankia.util.EventBuilder;
import com.dxc.bankia.util.VehicleBuilder;
import org.drools.compiler.compiler.DrlParser;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.command.Command;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.dxc.bankia.model.functions.DateUtils.toDate;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


/**
 *
 * @author esteban
 */
public class RuleEventEnrichmentTest extends BaseTest {

    /**
     * Tests customer-classification-simple.drt template using the configuration
     * present in kmodule.xml.
     */
    @Test
    public void testSimpleTemplateWithSpreadsheet1(){


        KieServices ks = KieServices.Factory.get();

        // works even without -SNAPSHOT versions

        ///kie/global/com/dxc/bankia/traffic-enrichment-rules-kjar/1.0.0-SNAPSHOT
        //String url = "http://localhost:8080/kie-drools/maven2/com/dxc/bankia/traffic-enrichment-rules-kjar/1.2.3/Test-1.2.3.jar";
        //String url = "http://localhost:7080/business-central/maven2/kie/global/com/dxc/bankia/traffic-enrichment-rules-kjar//1.0.0-SNAPSHOT/traffic-enrichment-rules-kjar-1.0.0-20191017.091438-1.jar";
        //String url = "http://localhost:7080/business-central/maven2/com/dxc/bankia/traffic-enrichment-rules-kjar//1.0.0-SNAPSHOT/traffic-enrichment-rules-kjar-1.0.0-20191017.091438-1.jar";
        //String url = "http://localhost:8081/repository/maven-public/com/dxc/bankia/traffic-enrichment-rules-kjar/1.0.0.jar";

        //ks.getResources().newUrlResource(url);

        //GAV.
        String groupId = "com.dxc.bankia";
        String artifactId = "traffic-enrichment-rules-kjar";
        String version = "1.2.0";

        //KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(groupId, artifactId, version);

        //KieSession ksession = this.createSession("enrichmentSession",releaseId);
        KieSession ksession = this.createSession("enrichmentSession",releaseId);

        FinderService finderService=getFinderSerrvice();

        OutputChannelImpl errorServiceChannel = new OutputChannelImpl();
        OutputChannelImpl filterServiceChannel = new OutputChannelImpl();

        ksession.setGlobal("finderService", finderService);
        ksession.registerChannel("error-channel",errorServiceChannel);
        ksession.registerChannel("filter-channel",filterServiceChannel);

        this.doTest(ksession,errorServiceChannel.getMessages());
    }


    /**
     * Tests customer-classification-simple.drt template using the configuration
     * present in kmodule.xml.
     */
    @Test
    public void testStatelessSession(){


        KieServices ks = KieServices.Factory.get();

        //GAV.
        String groupId = "com.dxc.bankia";
        String artifactId = "traffic-enrichment-rules-kjar";
        String version = "1.2.0";

        //KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(groupId, artifactId, version);

        //KieSession ksession = this.createSession("enrichmentSession",releaseId);
        KieSession ksession = this.createSession("enrichmentSession",releaseId);
        FinderService finderService=getFinderSerrvice();


        //Implement a Channel that notifies AuditService when new instances of
        //SuspiciousOperation are available.
        OutputChannelImpl errorServiceChannel = new OutputChannelImpl();
        OutputChannelImpl filterServiceChannel = new OutputChannelImpl();

        ksession.setGlobal("finderService", finderService);
        ksession.registerChannel("error-channel",errorServiceChannel);
        ksession.registerChannel("filter-channel",filterServiceChannel);

        this.doTestStateless(ks,ksession,errorServiceChannel.getMessages());
    }




    private void doTestStateless(KieServices ks, KieSession statelessKieSession,List<String> results){

        Event event1 = new EventBuilder()
                .withId(1L)
                .withType(Event.Type.REQUEST_CAR_ITV_COMPLIANCE)
                .withRegistrationNumber("XSC 1234")
                .build();

        Event event2 = new EventBuilder()
                .withId(2L)
                .withType(Event.Type.REQUEST_CAR_ITV_COMPLIANCE)
                .withRegistrationNumber("XSC 66666")
                .build();

        Event event3 = new EventBuilder()
                .withId(3L)
                .withType(Event.Type.REQUEST_DRIVER_ITV_COMPLIANCE)
                .withIdentificationNumber("A3456737X")
                .build();

        Event event4 = new EventBuilder()
                .withId(4L)
                .withType(Event.Type.REQUEST_DRIVER_ITV_COMPLIANCE)
                .withIdentificationNumber("VD345737X")
                .build();

        Command newInsertEvent = ks.getCommands().newInsert(event1, "eventOut");
        //Command newInsertCustomer = ks.getCommands().newInsert(customer);
        Command newFireAllRules = ks.getCommands().newFireAllRules("outFired");
        List<Command> cmds = new ArrayList<Command>();
        cmds.add(newInsertEvent);
        //cmds.add(newInsertCustomer);
        cmds.add(newFireAllRules);
        ExecutionResults execResults = statelessKieSession.execute(ks.getCommands().newBatchExecution(cmds));

        event1 = (Event)execResults.getValue("orderOut");
        int fired = (Integer)execResults.getValue("outFired");


    }




    private void doTest(KieSession ksession,List<String> results){

        Event event1 = new EventBuilder()
                .withId(1L)
                .withType(Event.Type.REQUEST_CAR_ITV_COMPLIANCE)
                .withRegistrationNumber("XSC 1234")
                .build();

        Event event2 = new EventBuilder()
                .withId(2L)
                .withType(Event.Type.REQUEST_CAR_ITV_COMPLIANCE)
                .withRegistrationNumber("XSC 66666")
                .build();

        Event event3 = new EventBuilder()
                .withId(3L)
                .withType(Event.Type.REQUEST_DRIVER_ITV_COMPLIANCE)
                .withIdentificationNumber("A3456737X")
                .build();

        Event event4 = new EventBuilder()
                .withId(4L)
                .withType(Event.Type.REQUEST_DRIVER_ITV_COMPLIANCE)
                .withIdentificationNumber("VD345737X")
                .build();


        ksession.insert(event1);
        ksession.insert(event2);
        ksession.insert(event3);
        ksession.insert(event4);

        ksession.fireAllRules();

        assertThat(event1.getVehicle(), not(nullValue()));
        assertThat(event2.getVehicle(), nullValue());
        assertThat(event3.getDriver(), not(nullValue()));
        assertThat(event4.getDriver(), nullValue());


        Assert.assertThat(results, hasSize(2));
        /*
        Assert.assertThat(
                results.stream().map(so -> so.getId()).collect(toList())
                , containsInAnyOrder( 2L,4L)
        );*/


    }

    private KieSession createKieSessionFromDRL(String drl){
        KieHelper kieHelper = new KieHelper();
        kieHelper.addContent(drl, ResourceType.DRL);

        Results results = kieHelper.verify();

        if (results.hasMessages(Message.Level.WARNING, Message.Level.ERROR)){
            List<Message> messages = results.getMessages(Message.Level.WARNING, Message.Level.ERROR);
            for (Message message : messages) {
                System.out.println("Error: "+message.getText());
            }

            throw new IllegalStateException("Compilation errors were found. Check the logs.");
        }

        return kieHelper.build().newKieSession();
    }

    /**
     * Tests customer-classification-simple.drt template by manually creating
     * the corresponding DRL using a spreadsheet as the data source.
     */
    public void testSimpleTemplateWithSpreadsheet2() throws Exception{

        ClassLoader classLoader = ClassLoader.getSystemClassLoader();

        File dslrFile = new File(classLoader.getResource("com.dxc.bankia/event/event-enrichment.drl").getFile());

        String dslrContent = new String(Files.readAllBytes(dslrFile.toPath()));
        System.out.println(dslrContent);


        File dslFile = new File(classLoader.getResource("com.dxc.bankia/event/event-enrichment.dsl").getFile());

        String dslContent = new String(Files.readAllBytes(dslFile.toPath()));
        System.out.println(dslContent);

        InputStream dsl = RuleEventEnrichmentTest.class.getResourceAsStream("/com.dxc.bankia/event/event-enrichment.dsl");

        DrlParser parser = new DrlParser();
        String drl = parser.getExpandedDRL(  dslrContent, new StringReader(dslContent) );

        System.out.println(drl);

        KieSession ksession = this.createKieSessionFromDRL(drl);

        this.doTest(ksession,null);
    }


     private FinderService getFinderSerrvice(){

        return  new FinderService() {

            @Override
            public Vehicle getVehicleByRegistrationNumber(String registrationNumber) {
                switch (registrationNumber){
                    case "XSC 1234":
                        return new VehicleBuilder()
                                .withId(1L)
                                .withModel("320 d")
                                .withCategory(Vehicle.Category.CAR)
                                .withColor(Vehicle.Color.BLUE)
                                .withBrand(Vehicle.Brand.BMW)
                                .withRegistrationNumber("XSC 1234")
                                .withCountry(Country.ES)
                                .withLastItvDate(toDate(LocalDate.of(2005,7,23)))
                                .withRegistrationDate(toDate(LocalDate.of(1999,7,23)))
                                .build();
                    case "BBB 324":
                        return new VehicleBuilder()
                                .withId(2L)
                                .withModel("320 d")
                                .withCategory(Vehicle.Category.CAR)
                                .withColor(Vehicle.Color.BLUE)
                                .withBrand(Vehicle.Brand.BMW)
                                .withRegistrationNumber("BBB 324")
                                .withCountry(Country.ES)
                                .withLastItvDate(toDate(LocalDate.of(2017,7,23)))
                                .withRegistrationDate(toDate(LocalDate.of(1999,7,23)))
                                .build();
                    case "LL 231":
                        return new VehicleBuilder()
                                .withId(3L)
                                .withModel("125 city")
                                .withCategory(Vehicle.Category.BIKE)
                                .withColor(Vehicle.Color.RED)
                                .withBrand(Vehicle.Brand.BMW)
                                .withRegistrationNumber("LL 231")
                                .withCountry(Country.ES)
                                .withRegistrationDate(toDate(LocalDate.of(2018,7,23)))
                                .build();
                    default:
                        return null;
                }
            }



            @Override
            public Driver getDriverByIdentificationNumber(String identificationNumber) {
                switch (identificationNumber){
                    case "A3456737X":
                        return new DriverBuilder()
                                .withId(1L)
                                .withIdentificationNumber("A3456737X")
                                .withLicenseNumber("LT 1234")
                                .withName("Jose","Smith")
                                .withDateOfBirth(toDate(LocalDate.of(1968,4,10)))
                                .withNationality(Country.ES)
                                .withVehicle(getVehicleByRegistrationNumber("XSC 1234"))
                                .withVehicle(getVehicleByRegistrationNumber("LL 231"))
                                .build();
                    default:
                        return null;
                }
            }
        };

    }


    private Channel getChannel(){

        final Set<Event> results = new HashSet<>();
        return  new Channel(){

            @Override
            public void send(Object object) {
                //notify AuditService here. For testing purposes, we are just
                //going to store the received object in a Set.
                results.add((Event) object);
            }

        };

    }



}
