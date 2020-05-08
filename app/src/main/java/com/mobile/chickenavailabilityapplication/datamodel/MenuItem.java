package com.mobile.chickenavailabilityapplication.datamodel;

import java.io.Serializable;

/**
 * Created by Harsha reddy on 5/4/20
 */
public class MenuItem implements Serializable {
    public int id;
    public String itemHeading;
    public String itemSubheading;
    public String itemImage;
    public int availableQuantity;
    public double price ;

    public MenuItem() {

    }

    public MenuItem(int id, String itemHeading, String itemSubheading, String itemImage, int availableQuantity, double price) {
        this.id = id;
        this.itemHeading = itemHeading;
        this.itemSubheading = itemSubheading;
        this.itemImage = itemImage;
        this.availableQuantity = availableQuantity;
        this.price = price;
    }
}
