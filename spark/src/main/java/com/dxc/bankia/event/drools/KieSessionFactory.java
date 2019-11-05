/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dxc.bankia.event.drools;

import com.dxc.bankia.services.FinderService;
import com.dxc.bankia.services.FinderServiceImpl;
import com.dxc.bankia.services.OutputChannelImpl;
import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lazy container kieContainer initialization
 * @author Dennis Federico
 */
public class KieSessionFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(KieSessionFactory.class);

    //Lazy - Since KieContainers are not reusable, should we keep a refernce to a kieBase instead?
    private KieContainer kieContainer;
    private KieScanner kieScanner;
    private StatelessKieSession statelesskieSession;

    private KieSessionFactory() {
        String groupId = "com.dxc.bankia";
        String  artifactId = "traffic-enrichment-rules-kjar";
        String  version = "1.2.0";

        kieContainer = buildKieContainer();
        kieScanner = registerKieScanner(kieContainer);
        statelesskieSession = null;
    }

    private static class KieSessionFactoryHolder {

        static final KieSessionFactory INSTANCE = new KieSessionFactory();
    }

    public static StatelessKieSession getNewKieSession() {

        FinderService finderService=new FinderServiceImpl();
        OutputChannelImpl errorServiceChannel = new OutputChannelImpl();
        OutputChannelImpl filterServiceChannel = new OutputChannelImpl();
        StatelessKieSession statelessKieSession = KieSessionFactoryHolder.INSTANCE.kieContainer.newStatelessKieSession();
        statelessKieSession.setGlobal("finderService", finderService);
        statelessKieSession.registerChannel("error-channel",errorServiceChannel);
        statelessKieSession.registerChannel("filter-channel",filterServiceChannel);

        return statelessKieSession;

    }

    private static KieContainer buildKieContainer() {
        System.setProperty("kie.maven.settings.custom", "/root/.m2/settings.xml");
        KieServices kieServices = KieServices.get();
        LOGGER.error("IS KIE-SERVICE ON ??" + (kieServices != null));
        ReleaseId rulesReleaseId = kieServices.newReleaseId("com.bankia.drools", "drools-poc-rules", "1.0.0-SNAPSHOT");
        LOGGER.error("IS KIE-rulesReleaseId ON ??" + (rulesReleaseId != null));
        KieContainer kContainer = kieServices.newKieContainer(rulesReleaseId);
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
