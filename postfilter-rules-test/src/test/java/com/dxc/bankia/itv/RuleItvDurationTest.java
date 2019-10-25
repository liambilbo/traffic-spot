/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dxc.bankia.itv;


import com.dxc.bankia.BaseTest;
import com.dxc.bankia.model.Country;
import com.dxc.bankia.model.Vehicle;
import com.dxc.bankia.util.VehicleBuilder;
import org.drools.decisiontable.ExternalSpreadsheetCompiler;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

import static com.dxc.bankia.model.functions.DateUtils.toDate;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;


/**
 *
 * @author esteban
 */
public class RuleItvDurationTest extends BaseTest {

    /**
     * Tests customer-classification-simple.drt template using the configuration
     * present in kmodule.xml.
     */
    @Test
    public void testSimpleTemplateWithSpreadsheet1(){

        KieServices ks = KieServices.Factory.get();

        String groupId = "com.dxc.bankia";
        String artifactId = "traffic-postfilter-rules-kjar";
        String version = "1.2.0";

        //KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(groupId, artifactId, version);

        KieSession ksession = this.createSession("postFilterSession",null);

        this.doTest(ksession);
    }



    private void doTest(KieSession ksession){
        Vehicle vehicle1 = new VehicleBuilder()
                .withId(1L)
                .withModel("320 d")
                .withCategory(Vehicle.Category.CAR)
                .withColor(Vehicle.Color.BLUE)
                .withBrand(Vehicle.Brand.BMW)
                .withRegistrationNumber("XSC 1234")
                .withCountry(Country.ES)
                .withRegistrationDate(toDate(LocalDate.of(1999,7,23)))
                .build();


        Vehicle vehicle2 = new VehicleBuilder()
                .withId(2L)
                .withModel("320 d")
                .withCategory(Vehicle.Category.CAR)
                .withColor(Vehicle.Color.BLUE)
                .withBrand(Vehicle.Brand.BMW)
                .withRegistrationNumber("BBB 324")
                .withCountry(Country.ES)
                .withRegistrationDate(toDate(LocalDate.of(2016,7,23)))
                .build();



        ksession.insert(vehicle1);
        ksession.insert(vehicle2);

        ksession.fireAllRules();

        assertThat(vehicle1.getNextItvDate(), not(nullValue()));

        assertThat(vehicle2.getNextItvDate(), not(nullValue()));


    }


}
