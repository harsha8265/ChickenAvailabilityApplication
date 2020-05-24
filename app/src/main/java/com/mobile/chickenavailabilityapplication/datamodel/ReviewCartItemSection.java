package com.mobile.chickenavailabilityapplication.datamodel;

import java.io.Serializable;

/**
 * Created by Harsha reddy on 5/23/20
 */
public class ReviewCartItemSection implements Serializable {
    public boolean isSection;
    public String category;

    public ReviewCartItemSection( boolean isSection, String category) {
        this.isSection = isSection;
        this.category = category;
    }
}
