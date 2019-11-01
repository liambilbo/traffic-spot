package com.dxc.bankia.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class VehicleLicense implements Serializable {

    public enum Type {
        B1, B2
    }



    private Long id;
    private String identificationNumber;
    private Type type;
    private Date inceptionDate;

    public VehicleLicense(){

    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Date getInceptionDate() {
        return inceptionDate;
    }

    public void setInceptionDate(Date inceptionDate) {
        this.inceptionDate = inceptionDate;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VehicleLicense)) return false;
        VehicleLicense that = (VehicleLicense) o;
        return getId().equals(that.getId()) &&
                getIdentificationNumber() == that.getIdentificationNumber() &&
                getType() == that.getType() &&
                getInceptionDate().equals(that.getInceptionDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getType(), getInceptionDate());
    }


}
