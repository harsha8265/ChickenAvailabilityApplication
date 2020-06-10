package com.mobile.chickenavailabilityapplication.datamodel;

import android.net.Network;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.widget.Toast;

import com.mobile.chickenavailabilityapplication.datastore.FileSystemConnector;
import com.mobile.chickenavailabilityapplication.network.NetworkConstants;
import com.mobile.chickenavailabilityapplication.network.NetworkHandler;
import com.mobile.chickenavailabilityapplication.util.PostNotification;
import com.mobile.chickenavailabilityapplication.util.ViewUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Harsha on 05/02/20.
 */
public class Customer extends Handler implements Serializable {

    private static Customer sCustomer = newSingleton();


    private static final String CustomerClassKey = "Customer";
    public static final String CustomerIdKey = "customerId";
    public static final String CustomerStatusKey = "role";
    private static final String FirstNameKey = "firstName";
    private static final String LastNameKey = "lastName";
    private static final String EmailKey = "emailAddress";
    private static final String PasswordKey = "password";
    private static final String PinKey = "pin";
    private static final String CellNumberKey = "cellNumber";
    private static final String SecurityQuestionIdKey = "securityQuestion";
    private static final String SecurityAnswerKey = "answer";
    private static final String RegistrationDateTimeKey = "registeredDate";
    private static final String DoorNumberKey = "doorNumber";
    private static final String StreetKey = "street";
    private static final String AreaKey = "area";
    private static final String PinCodeKey = "pinCode";
    private static final String TownKey = "town";
    private static final String StateKey = "state";



    //public static String sKey="jLIjfQZ5yojbZGTqxg2pYw==";
    public transient static String sKey;
    public transient Handler mHandler;

    public String customerId;
    public String accessCode;
    public String firstName;
    public String lastName;
    public String email;
    public String password;
    public String pin;
    public String cellNumber;
    public int securityQuestionId;
    public String securityAnswer;
    public Date registrationDateTime;
    public String timezoneId;
    public Address shippingAddress;
    public Address billingAddress;
    public ArrayList<ProfileKeyValue> mProfileKeyValues;
    public String PaymentMethod;


    private static Customer newSingleton() {

        if (sKey == null) {
            createKey();
        }

        FileSystemConnector fileSystemConnector = new FileSystemConnector();
        try {
            //Object customer = fileSystemConnector.read(CustomerClassKey, true, sKey);
            sCustomer = (Customer) fileSystemConnector.read(CustomerClassKey, true, sKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (sCustomer == null) {
            sCustomer = new Customer();
        }

        return sCustomer;
    }

    public static void createKey() {
        byte[] key = new byte[0];
        MessageDigest sha = null;

        try {
            key = ("12345").getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16); // use only first 128 bit

        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        sKey = Base64.encodeToString(secretKeySpec.getEncoded(), Base64.DEFAULT);
        sKey = sKey.trim();
    }

    public static Customer getInstance() {
        return sCustomer;
    }

    public void saveObject() {
        FileSystemConnector fileSystemConnector = new FileSystemConnector();
        try {
            fileSystemConnector.write(this, CustomerClassKey, sKey, true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }



    public void saveUserDetails(Handler handler) {
        this.mHandler = handler;
        NetworkHandler networkHandler = new NetworkHandler(this);
        networkHandler.ProcessRequest(getUserDetailsNetworkObject());
    }

    public void loginUser(String cellNumber, String userPin, Handler handler) {
        this.mHandler = handler;
        this.cellNumber = cellNumber;
        this.pin = userPin;
        NetworkHandler networkHandler = new NetworkHandler(this);
        networkHandler.ProcessRequest(getLoginUserNetworkObject());
    }



    @NotNull
    private NetworkObject getUserDetailsNetworkObject() {
        NetworkObject obj = new NetworkObject();
        obj.mId = NetworkConstants.CUSTOMER_DETAILS_SAVE_SUCCESS;
        obj.mRequestUrl = NetworkConstants.SAVE_CUSTOMER_URL;
        obj.mRequestJson = "{\"" + FirstNameKey +  "\":\"" + firstName + "\", \""
                + LastNameKey +  "\":\"" + lastName + "\", \""
                + CellNumberKey +  "\":\"" + cellNumber + "\", \""
                + EmailKey +  "\":\"" + email + "\", \""
                + PasswordKey +  "\":\"" + password + "\", \""
                + PinKey +  "\":\"" + pin + "\", \""
                + CustomerStatusKey +  "\":" + "\"USER\"" + ", \""
                + SecurityQuestionIdKey +  "\":" + securityQuestionId + ", \""
                + SecurityAnswerKey +  "\":\"" + securityAnswer + "\"}";
        return obj;
    }


    private NetworkObject getLoginUserNetworkObject() {
        NetworkObject obj = new NetworkObject();
        obj.mId = NetworkConstants.CUSTOMER_LOGIN_SUCCESS;
        obj.mRequestUrl = NetworkConstants.LOGIN_CUSTOMER_URL;
        obj.mRequestJson = "{\"" + CellNumberKey + "\":\"" + cellNumber + "\", \""
                + PinKey +  "\":\"" + pin + "\"}";
        return obj;
    }

    public void sendCustomer(Customer customer) {
        NetworkHandler networkHandler = new NetworkHandler(this);
        networkHandler.ProcessRequest(getCustomerNetworkObject(customer));
    }

    private NetworkObject getCustomerNetworkObject(Customer user) {
        NetworkObject obj = new NetworkObject();
        obj.mId = NetworkConstants.CUSTOMER_DETAILS_UPDATE_SUCCESS;
        obj.mRequestUrl = NetworkConstants.SAVE_CUSTOMER_URL;
        obj.mRequestJson = user.getJson();
        obj.mRequestObject = user;
        return obj;
    }
    public String getJson() {
        return "{\"" + Customer.CustomerIdKey + "\":" + Customer.getInstance().customerId + ", \""
                + FirstNameKey +  "\":" + Customer.getInstance().firstName + ", \""
                + CellNumberKey +  "\":" + Customer.getInstance().cellNumber+ ", \""
                + EmailKey +  "\":" + Customer.getInstance().email+ ", \""
                + PasswordKey +  "\":\"" + Customer.getInstance().password + "\"}";
    }

    public void updateCustomerProfile(Customer customer,Handler handler) {
        this.mHandler=handler;
        NetworkHandler networkHandler = new NetworkHandler(this);
        networkHandler.ProcessRequest(updatePatientProfileObject(customer));
    }

    public void updateCellNumber(Customer customer, Handler handler){
        this.mHandler=handler;
        NetworkObject obj = new NetworkObject();
        obj.mId = NetworkConstants.CUSTOMER_DETAILS_UPDATE_SUCCESS;
        obj.mRequestUrl = NetworkConstants.UPDATE_CUSTOMER_URL+"updateCellNumber?id="+customer.customerId;
        obj.mRequestJson= "{\""+customer.cellNumber+"\"}";
        NetworkHandler networkHandler = new NetworkHandler(this);
        networkHandler.ProcessRequest(obj);
    }

    public void updateName(Customer customer, Handler handler){
        this.mHandler=handler;
        NetworkObject obj = new NetworkObject();
        obj.mId = NetworkConstants.CUSTOMER_DETAILS_UPDATE_SUCCESS;
        obj.mRequestUrl = NetworkConstants.UPDATE_CUSTOMER_URL+"updateShippingAddress?id="+customer.customerId;
        obj.mRequestJson= "{\"" + FirstNameKey + "\":" + customer.firstName + ", \""
                + LastNameKey +  "\":\"" + customer.lastName + "\"}";
        NetworkHandler networkHandler = new NetworkHandler(this);
        networkHandler.ProcessRequest(obj);
    }

    public void updateShippingAddress(Customer customer, Handler handler){
        this.mHandler=handler;
        NetworkObject obj = new NetworkObject();
        obj.mId = NetworkConstants.CUSTOMER_DETAILS_UPDATE_SUCCESS;
        obj.mRequestMethod = "PUT";
        obj.mRequestUrl = NetworkConstants.UPDATE_CUSTOMER_URL+"updateShippingAddress?id="+customer.customerId;
        obj.mRequestJson= "{\"" + DoorNumberKey + "\":\"" + customer.shippingAddress.DoorNumber + "\", \""
                + StreetKey +  "\":\"" + customer.shippingAddress.Street + "\", \""
                + AreaKey +  "\":\"" + customer.shippingAddress.Area + "\", \""
                + PinCodeKey +  "\":\"" + customer.shippingAddress.PinCode + "\", \""
                + TownKey +  "\":\"" + customer.shippingAddress.Town + "\", \""
                + StateKey +  "\":\"" + customer.shippingAddress.State + "\"}";
        NetworkHandler networkHandler = new NetworkHandler(this);
        networkHandler.ProcessRequest(obj);
    }

    private NetworkObject updatePatientProfileObject(Customer customer) {
        NetworkObject obj = new NetworkObject();
        obj.mId = NetworkConstants.CUSTOMER_DETAILS_UPDATE_SUCCESS;
        obj.mRequestUrl = NetworkConstants.UPDATE_CUSTOMER_URL;
        obj.mRequestJson = "{\"" + CustomerIdKey + "\":" + customerId + ", \""
                + FirstNameKey +  "\":\"" + firstName + "\", \""
                + LastNameKey +  "\":\"" + lastName + "\", \""
                + CellNumberKey +  "\":\"" + cellNumber + "\", \""
                + PinKey +  "\":\"" + pin + "\", \""
                + EmailKey +  "\":\"" + email + "\", \""
                + PasswordKey +  "\":" + password + ", \""
                + CustomerStatusKey +  "\":" + "user" + ", \""
                + SecurityQuestionIdKey +  "\":" + securityQuestionId + ", \""
                + SecurityAnswerKey +  "\":\"" + securityAnswer + "\"}";
        return obj;
    }

    public void updatePinSecurity(Customer customer,Handler handler) {
        this.mHandler=handler;
        NetworkHandler networkHandler = new NetworkHandler(this);
        networkHandler.ProcessRequest(updatePinSecurityObject(customer));
    }

    @NotNull
    private NetworkObject updatePinSecurityObject(Customer customer) {
        NetworkObject obj = new NetworkObject();
        obj.mId = NetworkConstants.CUSTOMER_PIN_SECURITY_UPDATE_SUCCESS;
        obj.mRequestUrl = NetworkConstants.UPDATE_CUSTOMER_URL;
        obj.mRequestJson = "{\"" + CustomerIdKey + "\":" + customerId + ", \""
                + FirstNameKey +  "\":\"" + firstName + "\", \""
                + LastNameKey +  "\":\"" + lastName + "\", \""
                + CellNumberKey +  "\":\"" + cellNumber + "\", \""
                + PinKey +  "\":\"" + pin + "\", \""
                + EmailKey +  "\":\"" + email + "\", \""
                + PasswordKey +  "\":" + password + ", \""
                + SecurityQuestionIdKey +  "\":" + securityQuestionId + ", \""
                + SecurityAnswerKey +  "\":\"" + securityAnswer + "\"}";
        return obj;
    }

    public void FillAccountDetails(){
        mProfileKeyValues = new ArrayList<>();
        SecurityQuestion sc = new SecurityQuestion(this.securityQuestionId, this.securityAnswer);
        mProfileKeyValues.add(new ProfileKeyValue("First Name",this.firstName));
        mProfileKeyValues.add(new ProfileKeyValue("Last Name",this.lastName));
        mProfileKeyValues.add(new ProfileKeyValue("Password",this.password));
        mProfileKeyValues.add(new ProfileKeyValue("Email Address",this.email));
        mProfileKeyValues.add(new ProfileKeyValue("Cell Number",this.cellNumber));
        mProfileKeyValues.add(new ProfileKeyValue("Payment Method",this.PaymentMethod));
        mProfileKeyValues.add(new ProfileKeyValue("Pin",this.pin));
        if(sc!=null){
            mProfileKeyValues.add(new ProfileKeyValue("Security Question",sc.toString()));
        }
        if(shippingAddress!=null){
            mProfileKeyValues.add(new ProfileKeyValue("Shipping Address",this.shippingAddress.toString()));
        }
        if(billingAddress!=null){
            mProfileKeyValues.add(new ProfileKeyValue("Billing Address",this.billingAddress.toString()));
        }
    }

    public void GetCustomerInformation (Handler handler){
        mHandler = handler;
        NetworkObject obj = new NetworkObject();
        obj.mId = NetworkConstants.CUSTOMER_LOGIN_SUCCESS;
        obj.mRequestUrl = NetworkConstants.GET_CUSTOMER_URL + "/" + Customer.getInstance().email;
        NetworkHandler networkHandler = new NetworkHandler(this);
        networkHandler.ProcessRequest(obj);
    }

    public void DeSerializeCustomerResponseJson(String json){
        try{
            JSONObject jsonObject = new JSONObject(json);
            this.customerId = jsonObject.getString(CustomerIdKey);
            this.firstName = jsonObject.getString(FirstNameKey);
            this.lastName = jsonObject.getString(LastNameKey);
            this.cellNumber = jsonObject.getString(CellNumberKey);
            this.pin = jsonObject.getString(PinKey);
            this.securityQuestionId = jsonObject.getInt(SecurityQuestionIdKey);
            this.securityAnswer = jsonObject.getString(SecurityAnswerKey);
            String registrationDate = jsonObject.getString(RegistrationDateTimeKey);
            if(registrationDate !=null && registrationDate != ""){
                this.registrationDateTime = ViewUtils.getDateForFormat("yyyy-MM-dd'T'HH:mm:ss", registrationDate, "UTC");
                this.timezoneId = (ViewUtils.getTimezoneForDate(this.registrationDateTime)).getID();
                this.registrationDateTime = new Date(ViewUtils.getDateWithoutTimeInMillies(this.registrationDateTime, timezoneId));
            }
            if(!jsonObject.isNull("shippingAddress")){
                JSONObject shippingAddressObject = jsonObject.getJSONObject("shippingAddress");
                if(shippingAddressObject!=null){
                    shippingAddress = new Address(shippingAddressObject.getString("doorNumber"),
                            shippingAddressObject.getString("street"),
                            shippingAddressObject.getString("area"),
                            shippingAddressObject.getString("pinCode"),
                            shippingAddressObject.getString("town"),
                            shippingAddressObject.getString("state"));
                }
            }

            if(!jsonObject.isNull("billingAddress")){
                JSONObject billingAddressObject = jsonObject.getJSONObject("billingAddress");
                if(billingAddressObject!=null){
                    billingAddress = new Address(billingAddressObject.getString("doorNumber"),
                            billingAddressObject.getString("street"),
                            billingAddressObject.getString("area"),
                            billingAddressObject.getString("pinCode"),
                            billingAddressObject.getString("town"),
                            billingAddressObject.getString("state"));
                }
            }
            FillAccountDetails();

        }catch (JSONException e){
            PostNotification.sendMessage(NetworkConstants.CUSTOMER_OPERATION_FAILURE, json, mHandler);
        }
    }

    @Override
    public void handleMessage(@NotNull Message message) {
        NetworkObject object = (NetworkObject) message.obj;
        switch (message.what) {
            case NetworkConstants.CUSTOMER_LOGIN_SUCCESS:
                if (message.obj != null) {
                    String json = object.mResponseJson;
                    try {
                        DeSerializeCustomerResponseJson(json);
                        saveObject();
                        PostNotification.sendMessage(NetworkConstants.CUSTOMER_LOGIN_SUCCESS, this, mHandler);
                    } catch (Exception e) {
                        PostNotification.sendMessage(NetworkConstants.CUSTOMER_OPERATION_FAILURE, json, mHandler);
                    }
                }
                else {
                    PostNotification.sendMessage(NetworkConstants.CUSTOMER_OPERATION_FAILURE, "Invalid Credentials", mHandler);
                }
                break;
            case NetworkConstants.CUSTOMER_DETAILS_SAVE_SUCCESS:
                if (message.obj != null) {
                    String json = object.mResponseJson;
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        this.customerId = jsonObject.getString(CustomerIdKey);
                        String registrationDate = jsonObject.getString(RegistrationDateTimeKey);
                        this.registrationDateTime = ViewUtils.getDateForFormat("yyyy-MM-dd'T'HH:mm:ss", registrationDate, "UTC");
                        this.timezoneId = (ViewUtils.getTimezoneForDate(this.registrationDateTime)).getID();
                        this.registrationDateTime = new Date(ViewUtils.getDateWithoutTimeInMillies(this.registrationDateTime, timezoneId));
                        saveObject();
                        PostNotification.sendMessage(NetworkConstants.CUSTOMER_DETAILS_SAVE_SUCCESS, this, mHandler);
                    } catch (JSONException e) {
                        PostNotification.sendMessage(NetworkConstants.CUSTOMER_OPERATION_FAILURE, json, mHandler);
                    }
                }
                else {
                    PostNotification.sendMessage(NetworkConstants.CUSTOMER_OPERATION_FAILURE, "Invalid Access Code", mHandler);
                }
                break;

            case NetworkConstants.NETWORK_ERROR_ID:
                if (object.mError == null) {
                    object.mError = NetworkConstants.UNKNOWN_ERROR_MESSAGE;
                }
                PostNotification.sendMessage(NetworkConstants.CUSTOMER_OPERATION_FAILURE, object.mError, mHandler);
                break;

            case NetworkConstants.CUSTOMER_DETAILS_UPDATE_SUCCESS:
                if (message.obj != null) {
                    PostNotification.sendMessage(NetworkConstants.CUSTOMER_DETAILS_UPDATE_SUCCESS, this, mHandler);
                }

                break;

            default:
                break;
        }
    }
}