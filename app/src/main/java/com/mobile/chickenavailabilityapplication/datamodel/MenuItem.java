package com.mobile.chickenavailabilityapplication.datamodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Harsha reddy on 5/4/20
 */
public class MenuItem implements Serializable {
    public String productID;
    public String itemHeading;
    public String itemSubheading;
    public String itemImage;
    public int availableQuantity;
    public double price ;
    public HashMap<String, ArrayList<String>> optionsMap;

    public MenuItem() {

    }

    public MenuItem(String productID, String itemHeading, String itemSubheading, String itemImage, int availableQuantity, double price, HashMap<String,ArrayList<String>> optionsMap) {
        this.productID = productID;
        this.itemHeading = itemHeading;
        this.itemSubheading = itemSubheading;
        this.itemImage = itemImage;
        this.availableQuantity = availableQuantity;
        this.price = price;
        this.optionsMap=optionsMap;
    }


}
