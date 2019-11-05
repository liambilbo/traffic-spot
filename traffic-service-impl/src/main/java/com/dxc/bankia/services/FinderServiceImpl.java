package com.dxc.bankia.services;

import com.dxc.bankia.model.Country;
import com.dxc.bankia.model.Driver;
import com.dxc.bankia.model.Vehicle;
import com.dxc.bankia.util.DriverBuilder;
import com.dxc.bankia.util.VehicleBuilder;

import java.io.Serializable;
import java.time.LocalDate;

import static com.dxc.bankia.model.functions.DateUtils.toDate;

public class FinderServiceImpl implements  FinderService, Serializable {
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
            case "ES 217":
                return new VehicleBuilder()
                        .withId(3L)
                        .withModel("My jaguar")
                        .withCategory(Vehicle.Category.CAR)
                        .withColor(Vehicle.Color.BLUE)
                        .withBrand(Vehicle.Brand.JAGUAR)
                        .withRegistrationNumber("ES 217")
                        .withCountry(Country.GR)
                        .withRegistrationDate(toDate(LocalDate.of(1991,7,23)))
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

