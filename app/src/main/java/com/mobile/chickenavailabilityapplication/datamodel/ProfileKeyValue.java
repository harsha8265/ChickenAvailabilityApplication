package com.mobile.chickenavailabilityapplication.datamodel;

import java.io.Serializable;

public class ProfileKeyValue implements Serializable {
    public String Key;
    public String Value;
    public ProfileKeyValue(String Key, String Value) {
        this.Key = Key;
        this.Value = Value;
    }

    public ProfileKeyValue(){

    }
}
