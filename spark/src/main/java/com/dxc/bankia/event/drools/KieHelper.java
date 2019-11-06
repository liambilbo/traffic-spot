package com.dxc.bankia.event.drools;

import com.dxc.bankia.services.FinderServiceImpl;
import com.dxc.bankia.services.OutputChannelImpl;
import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class KieHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(KieHelper.class);

    private static TrafficStatelessSession instance = null;

    public static TrafficStatelessSession getInstance( String groupId,String  artifactId,String version,String sessionName) {
        if (instance == null) {

            KieContainer kieContainer =buildKieContainer(groupId,artifactId,version);
            KieScanner kieScanner = registerKieScanner(kieContainer);

             instance = new TrafficStatelessSession(kieContainer.newStatelessKieSession(sessionName));
        }
        return instance;
    }



    private static KieContainer buildKieContainer( String groupId,String  artifactId,String version) {
        System.setProperty("kie.maven.settings.custom", "/root/.m2/settings.xml");
        //KieServices kieServices = KieServices.get();
        KieServices kieServices = KieServices.Factory.get();
        LOGGER.error("IS KIE-SERVICE ON ??" + (kieServices != null));
        ReleaseId rulesReleaseId = kieServices.newReleaseId(groupId,artifactId,version);
        LOGGER.error("IS KIE-rulesReleaseId ON ??" + (rulesReleaseId != null));
        KieContainer kContainer = kieServices.newKieContainer(rulesReleaseId);

        Results results = kContainer.verify();

        if (results.hasMessages(Message.Level.WARNING, Message.Level.ERROR)){
            List<Message> messages = results.getMessages(Message.Level.WARNING, Message.Level.ERROR);
            for (Message message : messages) {
                System.out.printf("[%s] - %s[%s,%s]: %s", message.getLevel(), message.getPath(), message.getLine(), message.getColumn(), message.getText());
            }

            throw new IllegalStateException("Compilation errors were found. Check the logs.");
        }
        return kContainer;
    }

    private static KieScanner registerKieScanner(KieContainer kContainer) {
        KieServices kieServices = KieServices.get();
        KieScanner kScanner = kieServices.newKieScanner(kContainer);
        LOGGER.error("IS KIE-kScanner ON!! ??" + (kScanner != null));
        kScanner.scanNow();
        kScanner.start(30_000L); //Poll every 30 secs
        return kScanner;
    }
}


