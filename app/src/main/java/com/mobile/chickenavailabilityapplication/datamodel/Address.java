package com.mobile.chickenavailabilityapplication.datamodel;

import java.io.Serializable;

public class Address implements Serializable {
    public String DoorNumber;
    public String Street;
    public String Area;
    public String PinCode;
    public String Town;
    public String State;

    public Address(String doorNumber, String street, String area, String pinCode, String town, String state) {
        this.DoorNumber = doorNumber;
        this.Street = street;
        this.Area = area;
        this.PinCode = pinCode;
        this.Town = town;
        this.State = state;
    }

    public Address(){

    }

    @Override
    public String toString() {
        return DoorNumber + System.getProperty("line.separator")+
                Street + System.getProperty("line.separator")+
                Area + System.getProperty("line.separator")+
                PinCode + System.getProperty("line.separator")+
                Town + System.getProperty("line.separator")+
               State + System.getProperty("line.separator");
    }
}
