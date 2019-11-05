package com.dxc.bankia.event.functions;

import com.dxc.bankia.event.objects.EventExecuted;
import com.dxc.bankia.model.Event;
import com.dxc.bankia.services.FinderService;
import com.dxc.bankia.services.FinderServiceImpl;
import com.dxc.bankia.services.NotificationChannelImpl;
import com.dxc.bankia.services.OutputChannelImpl;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.command.Command;
import org.kie.api.runtime.*;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * Create Kie container , base and session
 * Apply enrichment rules
 *
 * @see KieSession
 * @see
 */
public class ApplyPostfilter extends BaseKieContainer implements FlatMapFunction<Iterator<Event>, EventExecuted> {


    private String groupId = "com.dxc.bankia";
    private String  artifactId = "traffic-postfilter-rules-kjar";
    private String  version = "1.0.0-SNAPSHOT";

    private FinderService finderService=new FinderServiceImpl();
    private OutputChannelImpl errorServiceChannel = new OutputChannelImpl();
    private OutputChannelImpl filterServiceChannel = new OutputChannelImpl();
    private NotificationChannelImpl notificationServiceChannel = new NotificationChannelImpl();

   private static class PostfilterContainerHolder {
        static final ApplyPostfilter instance = new ApplyPostfilter();
    }

    private KieScanner kieScanner;
    private KieContainer kieContainer;


    private ApplyPostfilter() {
        System.out.println("Start kie session");
        System.setProperty("kie.maven.settings.custom", "/root/.m2/settings.xml");
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(groupId, artifactId, version);
        kieContainer = ks.newKieContainer(releaseId);
        //The KieContainer is wrapped by a KieScanner.
        //Note that we are never starting the KieScanner because we want to control
        //when the upgrade process kicks in.
        kieScanner = ks.newKieScanner(kieContainer);
        kieScanner.start(30_000L);
        // kieScanner.scanNow();

        Results results = kieContainer.verify();

        if (results.hasMessages(Message.Level.WARNING, Message.Level.ERROR)){
            List<Message> messages = results.getMessages(Message.Level.WARNING, Message.Level.ERROR);
            for (Message message : messages) {
                System.out.printf("[%s] - %s[%s,%s]: %s", message.getLevel(), message.getPath(), message.getLine(), message.getColumn(), message.getText());
            }

            throw new IllegalStateException("Compilation errors were found. Check the logs.");
        }
    }

    public static ApplyPostfilter getInstance() {
        return PostfilterContainerHolder.instance;
    }

    @Override
    public Iterator<EventExecuted> call(Iterator<Event> rowIte) throws Exception {

        StatelessKieSession statelessKieSession = kieContainer.newStatelessKieSession("enrichmentStatelessSession");

        //FinderService finderService=new FinderServiceImpl
        //Get Blog type from kie base
        statelessKieSession.setGlobal("finderService", finderService);
        statelessKieSession.registerChannel("error-channel",errorServiceChannel);
        statelessKieSession.registerChannel("filter-channel",filterServiceChannel);
        statelessKieSession.registerChannel("notification-channel",notificationServiceChannel);
        KieServices ks = KieServices.Factory.get();
        List output = new ArrayList();

        System.out.println("Begining event iteration");

        while (rowIte.hasNext()) {
            Event event= rowIte.next();
            System.out.println("Adding event");


            Command newInsertOrder = ks.getCommands().newInsert(event, "eventOut");
            Command newFireAllRules = ks.getCommands().newFireAllRules("outFired");
            List<Command> cmds = new ArrayList<Command>();
            cmds.add(newInsertOrder);
            cmds.add(newFireAllRules);
            ExecutionResults execResults = statelessKieSession.execute(ks.getCommands().newBatchExecution(cmds));

            event = (Event)execResults.getValue("eventOut");
            int fired = (Integer)execResults.getValue("outFired");

            EventExecuted executed=new EventExecuted(event,fired);
            if (errorServiceChannel.hasMessages()) {
                errorServiceChannel.getMessages().stream().forEach((e) -> executed.addError(e));
                errorServiceChannel.getMessages().clear();
                executed.setWithErrors(true);
            }

            if (filterServiceChannel.hasMessages()) {
                filterServiceChannel.getMessages().stream().forEach((e) -> executed.addFiltered(e));
                filterServiceChannel.getMessages().clear();
                executed.setFiltered(true);
            }

            if (notificationServiceChannel.hasNotifications()) {
                notificationServiceChannel.getNotifications().stream().forEach((e) -> executed.addNotification(e));
                notificationServiceChannel.getNotifications().clear();
                executed.setWithNotifications(true);
            }

            output.add(executed);

        };
        System.out.println("Finishing  map partitions");
        return output.iterator();
    }
}

