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
    public static final String IdKey = "productId";
    public static final String ItemHeadingKey = "name";
    public static final String ItemImageKey = "imageUrl";
    public static final String ItemSubHeadingKey = "subHeading";
    public static final String AvailableQuantityKey = "quantity";
    public static final String PriceKey = "price";
    public static final String OptionsMapKey = "optionsMap";
    public static final String PieceOptions = "Piece";
    public static final String SkinOptions = "Skin";

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


    public void getMenuItems(Handler handler) {
        this.mHandler = handler;
        NetworkHandler networkHandler = new NetworkHandler(this);
        networkHandler.ProcessRequest(getInspirationsNetworkObject());
    }

    private NetworkObject getInspirationsNetworkObject() {
        NetworkObject obj = new NetworkObject();
        obj.mId = NetworkConstants.GET_MENUITEMS_SUCCESS;
        obj.mRequestUrl = NetworkConstants.PRODUCTS_URL;
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
                        JSONArray jsonarray = new JSONArray(json);
                        MenuItem menuItem=null;
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject jsonobject = jsonarray.getJSONObject(i);
                            menuItem=new MenuItem();
                            menuItem.productID= jsonobject.getString(IdKey);
                            menuItem.itemHeading = jsonobject.getString(ItemHeadingKey);
                            menuItem.itemSubheading=jsonobject.getString(ItemSubHeadingKey);
                            menuItem.imageUrl=jsonobject.getString(ItemImageKey);
                            menuItem.availableQuantity=jsonobject.getInt(AvailableQuantityKey);
                            menuItem.price=jsonobject.getInt(PriceKey);
                            JSONObject options = jsonobject.getJSONObject(OptionsMapKey);
                            HashMap<String,ArrayList<String>> completeOptionsMap = new HashMap<>();
                            if(options.length()!=0){
                                if(options.has(PieceOptions)){
                                    JSONArray pieceOptions = options.getJSONArray(PieceOptions);
                                    if(pieceOptions!=null && pieceOptions.length()!=0){
                                        ArrayList<String> pieceOptionsArray = new ArrayList<>();
                                        for (int j = 0; j<pieceOptions.length(); j++) {
                                            pieceOptionsArray.add(pieceOptions.getString(j));
                                        }
                                        completeOptionsMap.put(PieceOptions, pieceOptionsArray);
                                    }
                                }

                                if(options.has(SkinOptions)){
                                    JSONArray skinOptions = options.getJSONArray(SkinOptions);

                                    if(skinOptions!=null && skinOptions.length()!=0){
                                        ArrayList<String> skinOptionsArray = new ArrayList<>();
                                        for (int j = 0; j<skinOptions.length(); j++) {
                                            skinOptionsArray.add(skinOptions.getString(j));
                                        }
                                        completeOptionsMap.put(SkinOptions,skinOptionsArray);
                                    }
                                }
                            }
                            menuItem.optionsMap = completeOptionsMap;
                            menuItems.add(menuItem);
                        }
                        //saveObject();
                        PostNotification.sendMessage(NetworkConstants.GET_MENUITEMS_SUCCESS, this, mHandler);
                    }
                    catch (JSONException e) {
                        PostNotification.sendMessage(NetworkConstants.GET_MENUITEMS_FAILURE, json, mHandler);
                    }
                }
        }
    }
}

