package com.dxc.bankia.event.drools;

import com.dxc.bankia.services.FinderService;
import com.dxc.bankia.services.FinderServiceImpl;
import com.dxc.bankia.services.OutputChannelImpl;
import org.kie.api.KieServices;
import org.kie.api.runtime.StatelessKieSession;

public class TrafficStatelessSession {

    private FinderService finderService=new FinderServiceImpl();



    private OutputChannelImpl errorServiceChannel = new OutputChannelImpl();
    private OutputChannelImpl filterServiceChannel = new OutputChannelImpl();
    private StatelessKieSession statelessKieSession = null;

    public TrafficStatelessSession(StatelessKieSession statelessKieSession) {
        KieServices ks = KieServices.Factory.get();
        statelessKieSession = statelessKieSession;
        //FinderService finderService=new FinderServiceImpl
        //Get Blog type from kie base
        statelessKieSession.setGlobal("finderService", finderService);
        statelessKieSession.registerChannel("error-channel",errorServiceChannel);
        statelessKieSession.registerChannel("filter-channel",filterServiceChannel);

    }

    public FinderService getFinderService() {
        return finderService;
    }

    public void setFinderService(FinderService finderService) {
        this.finderService = finderService;
    }

    public OutputChannelImpl getErrorServiceChannel() {
        return errorServiceChannel;
    }

    public void setErrorServiceChannel(OutputChannelImpl errorServiceChannel) {
        this.errorServiceChannel = errorServiceChannel;
    }

    public OutputChannelImpl getFilterServiceChannel() {
        return filterServiceChannel;
    }

    public void setFilterServiceChannel(OutputChannelImpl filterServiceChannel) {
        this.filterServiceChannel = filterServiceChannel;
    }

    public StatelessKieSession getStatelessKieSession() {
        return statelessKieSession;
    }

    public void setStatelessKieSession(StatelessKieSession statelessKieSession) {
        this.statelessKieSession = statelessKieSession;
    }


}
