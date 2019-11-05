package com.dxc.bankia.event.functions;

import com.dxc.bankia.event.objects.EventExecuted;
import com.dxc.bankia.model.Event;
import com.dxc.bankia.services.FinderService;
import com.dxc.bankia.services.FinderServiceImpl;
import com.dxc.bankia.services.NotificationChannelImpl;
import com.dxc.bankia.services.OutputChannelImpl;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.command.Command;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;

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
    private String  version = "1.2.0";
    private FinderService finderService=new FinderServiceImpl();
    private OutputChannelImpl errorServiceChannel = new OutputChannelImpl();
    private OutputChannelImpl filterServiceChannel = new OutputChannelImpl();
    private NotificationChannelImpl notificationServiceChannel = new NotificationChannelImpl();

   public ApplyPostfilter(String groupId, String artifactId, String version){
        this.groupId=groupId;
        this.artifactId=artifactId;
        this.version=version;
    }
    @Override
    public Iterator<EventExecuted> call(Iterator<Event> rowIte) throws Exception {
       System.out.println("Start kie session");

        System.setProperty("kie.maven.settings.custom", "/root/.m2/settings.xml");
        //Initlized kie base
       // Logger LOGGER = LogManager.getLogger(ApplyEnrichment.class);
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(groupId, artifactId, version);
        StatelessKieSession statelessKieSession = this.createStatelessSession("postFilterStatelessSession",releaseId);
        //FinderService finderService=new FinderServiceImpl
        //Get Blog type from kie base
        statelessKieSession.setGlobal("finderService", finderService);
        statelessKieSession.registerChannel("error-channel",errorServiceChannel);
        statelessKieSession.registerChannel("filter-channel",filterServiceChannel);
        statelessKieSession.registerChannel("notification-channel",notificationServiceChannel);

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

