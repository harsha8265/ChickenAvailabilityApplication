package com.mobile.chickenavailabilityapplication.datamodel;

import com.mobile.chickenavailabilityapplication.datastore.FileSystemConnector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Harsha reddy on 5/9/20
 */
public class CartItemContainer implements Serializable {

    private static CartItemContainer cartItemContainer = newSingleton();
    private static final String CartItemKey = "cartitem";
    public static final String CartIdKey = "cartid";
    public static final String ProductIdKey = "productid";
    public static final String ProductNameKey = "productname";
    public static final String QuantitySelectedKey = "quantityselected";
    public static final String SubTotalkey = "subtotal";
    public static final String OptionsSelectedKey = "optionsselected";
    public static final String SpecialInstructionsRequestedkey = "specialinstructionsrequested";


    public ArrayList<CartItem> cartItems;

    public CartItemContainer() {
        cartItems = new ArrayList<CartItem>();

    }

    private static CartItemContainer newSingleton() {
        CartItemContainer cartItemContainer = null;
        FileSystemConnector fileSystemConnector = new FileSystemConnector();
        try {
            cartItemContainer = (CartItemContainer) fileSystemConnector.read(CartItemKey, true, Customer.sKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (cartItemContainer == null) {
            cartItemContainer = new CartItemContainer();
        }

        return cartItemContainer;
    }

    public static CartItemContainer readCartItemContainer() {
        return cartItemContainer;
    }

    public CartItem getCartItemForCartID(String cartID) {
        CartItem returnCartItem = null;
        for (CartItem cartItem : cartItems) {
            if (cartItem.cartID.equals(String.valueOf(cartID))) {
                returnCartItem = cartItem;
                break;
            }
        }
        return returnCartItem;
    }

    public void saveObject() {
        FileSystemConnector fileSystemConnector = new FileSystemConnector();
        try {
            fileSystemConnector.write(this, CartItemKey, Customer.sKey, true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void saveCartItems(String productID, String productName, double quantitySelected, double subTotal, HashMap<String,String> optionsSelected, String specialInstructionsrequested) {
        CartItem cartItem=new CartItem();
        cartItem.cartID= String.valueOf(UUID.randomUUID());
        cartItem.productID=productID;
        cartItem.productName=productName;
        cartItem.quantitySelected=quantitySelected;
        cartItem.subTotal=subTotal;
        cartItem.optionsSelected=optionsSelected;
        cartItem.specialInstructionsrequested=specialInstructionsrequested;
        cartItems.add(cartItem);
        saveObject();
    }

    public void editCartItems( String cartID,double quantitySelected, double subTotal, HashMap<String,String> optionsSelected, String specialInstructionsrequested){
        CartItem cartItem = readCartItemContainer().getCartItemForCartID(cartID);
        cartItem.quantitySelected=quantitySelected;
        cartItem.subTotal=subTotal;
        cartItem.optionsSelected=optionsSelected;
        cartItem.specialInstructionsrequested=specialInstructionsrequested;
        cartItems.remove(readCartItemContainer().getCartItemForCartID(cartID));
        cartItems.add(cartItem);
        saveObject();
    }

    public void deleteCartItemByCartID(String cartID){
        CartItem cartItem = readCartItemContainer().getCartItemForCartID(cartID);
        cartItems.remove(readCartItemContainer().getCartItemForCartID(cartID));
        saveObject();
    }

    public void emptyCart(){
        cartItems.clear();
        saveObject();
    }



}
