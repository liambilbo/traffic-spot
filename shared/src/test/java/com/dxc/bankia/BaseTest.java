/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dxc.bankia;

import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 *
 * @author esteban
 */
public class BaseTest {
    
    /**
     * For performance purposes, we keep a cached container. Please note that 
     * this is not Thread Safe at all!.
     */
    private static KieContainer cachedKieContainer;

    protected KieSession createDefaultSession() {
        return this.createContainer(null).newKieSession();
    }
    
    protected KieBase createKnowledgeBase(String name,ReleaseId releaseId) {
        KieContainer kContainer = this.createContainer(releaseId);
        KieBase kbase = kContainer.getKieBase(name);
        
        if (kbase == null){
            throw new IllegalArgumentException("Unknown Kie Base with name '"+name+"'");
        }
        
        return kbase;
    }

    protected KieSession createSession(String name,ReleaseId releaseId) {
        
        KieContainer kContainer = this.createContainer(releaseId);
        KieSession ksession = kContainer.newKieSession(name);
        
        if (ksession == null){
            throw new IllegalArgumentException("Unknown Session with name '"+name+"'");
        }
        
        return ksession;
    }

    protected <T> Collection<T> getFactsFromKieSession(KieSession ksession, Class<T> classType) {
        return (Collection<T>) ksession.getObjects(new ClassObjectFilter(classType));
    }

    protected StatelessKieSession createStatelessSession(String name, ReleaseId releaseId) {

        KieContainer kContainer = this.createContainer(releaseId);
        StatelessKieSession ksession = kContainer.newStatelessKieSession(name);

        if (ksession == null){
            throw new IllegalArgumentException("Unknown Session with name '"+name+"'");
        }

        return ksession;
    }

    
    private KieContainer createContainer(ReleaseId releaseId ){
        if (cachedKieContainer != null){
            return cachedKieContainer;
        }
        
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = null;
        if (releaseId==null) {
            kContainer = ks.getKieClasspathContainer();
        } else {
            kContainer = ks.newKieContainer(releaseId);
            //The KieContainer is wrapped by a KieScanner.
            //Note that we are never starting the KieScanner because we want to control
            //when the upgrade process kicks in.
            KieScanner scanner = ks.newKieScanner(kContainer);
            scanner.start(5000L);
            // alternatively:
            // kieScanner.scanNow();

        }

        Results results = kContainer.verify();
        
        if (results.hasMessages(Message.Level.WARNING, Message.Level.ERROR)){
            List<Message> messages = results.getMessages(Message.Level.WARNING, Message.Level.ERROR);
            for (Message message : messages) {
                System.out.printf("[%s] - %s[%s,%s]: %s", message.getLevel(), message.getPath(), message.getLine(), message.getColumn(), message.getText());
            }
            
            throw new IllegalStateException("Compilation errors were found. Check the logs.");
        }
        
        cachedKieContainer = kContainer;
        return cachedKieContainer;
    }

}
