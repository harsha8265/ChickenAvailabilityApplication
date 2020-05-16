package com.mobile.chickenavailabilityapplication.datamodel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Harsha reddy on 5/9/20
 */
public class CartItem implements Serializable {
    public String cartID;
    public String productID;
    public String productName;
    public double quantitySelected;
    public double subTotal;
    public HashMap<String,String> optionsSelected;
    public String specialInstructionsrequested;

    public CartItem() {
    }

    public CartItem(String productID, String productName, double quantitySelected, double subTotal, HashMap<String,String> optionsSelected, String specialInstructionsrequested) {
        this.productID = productID;
        this.productName = productName;
        this.quantitySelected = quantitySelected;
        this.subTotal = subTotal;
        this.optionsSelected = optionsSelected;
        this.specialInstructionsrequested = specialInstructionsrequested;
    }
}
