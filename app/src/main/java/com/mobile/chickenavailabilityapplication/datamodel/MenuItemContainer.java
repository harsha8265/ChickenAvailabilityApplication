package com.mobile.chickenavailabilityapplication.datamodel;

import android.os.Handler;
import android.os.Message;

import com.mobile.chickenavailabilityapplication.datastore.FileSystemConnector;
import com.mobile.chickenavailabilityapplication.network.NetworkConstants;
import com.mobile.chickenavailabilityapplication.network.NetworkHandler;
import com.mobile.chickenavailabilityapplication.util.PostNotification;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Harsha reddy on 5/4/20
 */

public class MenuItemContainer extends Handler implements Serializable {

    private static MenuItemContainer menuItemContainer = newSingleton();
    private static final String MenuItemKey = "menuitem";
    public static final String IdKey = "productid";
    public static final String ItemHeadingKey = "itemheading";
    public static final String ItemImageKey = "itemimage";
    public static final String ItemSubHeadingKey = "itemsubheading";
    public static final String AvailableQuantityKey = "availablequantity";
    public static final String PriceKey = "price";
    public static final String OptionsMapKey = "optionsmap";

    public ArrayList<MenuItem> menuItems;

    private transient Handler mHandler;


    public MenuItemContainer() {
        menuItems = new ArrayList<>();

    }

    private static MenuItemContainer newSingleton() {
        MenuItemContainer menuItemContainer = null;
        FileSystemConnector fileSystemConnector = new FileSystemConnector();
        try {
            menuItemContainer = (MenuItemContainer) fileSystemConnector.read(MenuItemKey, true, Customer.sKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (menuItemContainer == null) {
            menuItemContainer = new MenuItemContainer();
        }

        return menuItemContainer;
    }

    public static MenuItemContainer readMenuItemContainer() {
        return menuItemContainer;
    }

    public void saveObject() {
        FileSystemConnector fileSystemConnector = new FileSystemConnector();
        try {
            fileSystemConnector.write(this, MenuItemKey, Customer.sKey, true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public MenuItem getMenuListForProductId(String productID) {
        MenuItem returnMenuItem = null;
        for (MenuItem menuItem : menuItems) {
            if (menuItem.productID.equals(productID)) {
                returnMenuItem = menuItem;
                break;
            }
        }
        return returnMenuItem;
    }


    public void populateMenuItemObject() {
        HashMap<String,ArrayList<String>> completeOptionsMap = new HashMap<String,ArrayList<String>>();
        completeOptionsMap.put("Skin", new ArrayList<>(Arrays.asList("Skinless","Skin")));
        completeOptionsMap.put("Size", new ArrayList<>(Arrays.asList("Small","Medium","Large")));

        HashMap<String,ArrayList<String>> skinOptionsMap = new HashMap<String,ArrayList<String>>();
        skinOptionsMap.put("Skin", new ArrayList<>(Arrays.asList("Skinless","Skin")));

        HashMap<String,ArrayList<String>> sizeOptionsMap = new HashMap<String,ArrayList<String>>();
        sizeOptionsMap.put("Size", new ArrayList<>(Arrays.asList("Small","Medium","Large")));

        HashMap<String,ArrayList<String>> emptyOptions = new HashMap<String,ArrayList<String>>();


        MenuItem menuItem1 = new MenuItem("a1ef", "Chicken Thighs", "Chicken Thighs are the thigh of the chicken leg, just above the part of the leg called the drumstick", "chicken_thigh", 5,100.99,completeOptionsMap);
        MenuItem menuItem2 = new MenuItem("a2ef", "Chicken Legs", "Chicken Thighs are the thigh of the chicken leg, just above the part of the leg called the drumstick", "chicken_legs", 3,200.99,skinOptionsMap);
        MenuItem menuItem3 = new MenuItem("a3ef", "Whole Chicken", "Chicken Thighs are the thigh of the chicken leg, just above the part of the leg called the drumstick", "whole_chicken", 3,300.99,completeOptionsMap);
        MenuItem menuItem4 = new MenuItem("a4ef", "Chicken Liver", "Chicken Thighs are the thigh of the chicken leg, just above the part of the leg called the drumstick", "chicken_liver", 2,400.99,emptyOptions);
        MenuItem menuItem5 = new MenuItem("a5ef", "Chicken Quarters", "Chicken Thighs are the thigh of the chicken leg, just above the part of the leg called the drumstick", "chicken_legquarters", 4,500.99,skinOptionsMap);
        MenuItem menuItem6 = new MenuItem("a6ef", "Chicken Boneless", "Chicken Thighs are the thigh of the chicken leg, just above the part of the leg called the drumstick", "chicken_boneless", 7,600.99,sizeOptionsMap);

        menuItems.add(menuItem1);
        menuItems.add(menuItem2);
        menuItems.add(menuItem3);
        menuItems.add(menuItem4);
        menuItems.add(menuItem5);
        menuItems.add(menuItem6);

         saveObject();

    }

    public void getMenuItems(Handler handler) {
        this.mHandler = handler;
        NetworkHandler networkHandler = new NetworkHandler(this);
        networkHandler.ProcessRequest(getInspirationsNetworkObject());
    }

    private NetworkObject getInspirationsNetworkObject() {
        NetworkObject obj = new NetworkObject();
        obj.mId = NetworkConstants.GET_MENUITEMS_SUCCESS;
        obj.mRequestUrl = "";
        return obj;
    }

    @Override
    public void handleMessage(@NotNull Message msg) {
        NetworkObject object = (NetworkObject) msg.obj;
        switch (msg.what) {
            case NetworkConstants.GET_MENUITEMS_SUCCESS:
                if (msg.obj != null) {
                    String json = object.mResponseJson;
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        JSONArray jsonarray = jsonObject.getJSONArray("items");
                        MenuItem menuItem=null;
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject jsonobject = jsonarray.getJSONObject(i);
                            JSONObject jsonobject1 = jsonobject.getJSONObject("fields");
                            menuItem=new MenuItem();
                            menuItem.productID= jsonobject1.getString(IdKey);
                            menuItem.itemHeading = jsonobject1.getString(ItemHeadingKey);
                            menuItem.itemSubheading=jsonobject1.getString(ItemSubHeadingKey);
                            menuItem.itemImage=jsonobject1.getString(ItemImageKey);
                            menuItem.availableQuantity=jsonobject1.getInt(AvailableQuantityKey);
                            menuItem.price=jsonobject1.getInt(PriceKey);
                            menuItems.add(menuItem);

                        }
                        saveObject();
                        PostNotification.sendMessage(NetworkConstants.GET_MENUITEMS_SUCCESS, this, mHandler);
                    }
                    catch (JSONException e) {
                        PostNotification.sendMessage(NetworkConstants.GET_MENUITEMS_FAILURE, json, mHandler);
                    }
                }
        }
    }
}

