package com.mobile.chickenavailabilityapplication.datamodel;

import android.os.Handler;
import android.os.Message;
import android.util.Base64;

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
import java.util.Arrays;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Harsha on 05/02/20.
 */
public class Customer extends Handler implements Serializable {

    private static Customer sCustomer = newSingleton();

    public static final int GENDER_MALE = 1;
    public static final int GENDER_FEMALE = 2;

    private static final String CustomerClassKey = "Customer";
    public static final String CustomerIdKey = "customerid";
    public static final String CustomerStatusKey = "customerstatus";
    private static final String AccessCodeKey = "accesscode";
    private static final String FirstNameKey = "firstname";
    private static final String LastNameKey = "lastname";
    private static final String EmailKey = "email";
    private static final String PasswordKey = "password";
    private static final String PinKey = "pin";
    private static final String DOBKey = "dob";
    private static final String CellNumberKey = "cellnumber";
    private static final String GenderKey = "gender";
    private static final String SecurityQuestionIdKey = "securityquestion";
    private static final String SecurityAnswerKey = "answer";
    private static final String DoctorNameKey = "doctor_lastname";
    private static final String RegistrationDateTimeKey = "registrationdatetime";

    public transient static String sKey;
    public transient Handler mHandler;

    public int customerId;
    public String accessCode;
    public String firstName;
    public String lastName;
    public String email;
    public String password;
    public String pin;
    public String dob;
    public String cellNumber;
    public int gender;
    public int securityQuestionId;
    public String securityAnswer;
    public String doctorName;
    public Date registrationDateTime;
    public String timezoneId;

    private static Customer newSingleton() {

        if (sKey == null) {
            createKey();
        }

        FileSystemConnector fileSystemConnector = new FileSystemConnector();
        try {
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

    public void getUserDetails(String accesscode, Handler handler) {
        this.mHandler = handler;
        this.accessCode = accesscode;
        NetworkHandler networkHandler = new NetworkHandler(this);
        networkHandler.ProcessRequest(getAccessCodeNetworkObject());
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

    private NetworkObject getAccessCodeNetworkObject() {
        NetworkObject obj = new NetworkObject();
        obj.mId = NetworkConstants.ACCESS_CODE_SUCCESS;
        obj.mRequestUrl = NetworkConstants.GET_CUSTOMER_URL;
        obj.mRequestJson = "{\"" + AccessCodeKey + "\":\"" + accessCode + "\"}";
        return obj;
    }

    private NetworkObject getUserDetailsNetworkObject() {
        NetworkObject obj = new NetworkObject();
        obj.mId = NetworkConstants.CUSTOMER_DETAILS_SAVE_SUCCESS;
        obj.mRequestUrl = NetworkConstants.SAVE_CUSTOMER_URL;
        obj.mRequestJson = "{\"" + CustomerIdKey + "\":" + customerId + ", \""
                + FirstNameKey +  "\":\"" + firstName + "\", \""
                + LastNameKey +  "\":\"" + lastName + "\", \""
                + CellNumberKey +  "\":\"" + cellNumber + "\", \""
                + PinKey +  "\":\"" + pin + "\", \""
                + DOBKey +  "\":\"" + dob + "\", \""
                + GenderKey +  "\":" + gender + ", \""
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

    public void sendUser(Customer customer) {
        NetworkHandler networkHandler = new NetworkHandler(this);
        networkHandler.ProcessRequest(getUserNetworkObject(customer));
    }

    private NetworkObject getUserNetworkObject(Customer user) {
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
                + DOBKey +  "\":\"" + Customer.getInstance().dob + "\"}";
    }

    public void updatePatientProfile(Customer customer,Handler handler) {
        this.mHandler=handler;
        NetworkHandler networkHandler = new NetworkHandler(this);
        networkHandler.ProcessRequest(updatePatientProfileObject(customer));
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
                + DOBKey +  "\":\"" + dob + "\", \""
                + GenderKey +  "\":" + gender + ", \""
                + SecurityQuestionIdKey +  "\":" + securityQuestionId + ", \""
                + SecurityAnswerKey +  "\":\"" + securityAnswer + "\"}";
        return obj;
    }

    public void updatePinSecurity(Customer customer,Handler handler) {
        this.mHandler=handler;
        NetworkHandler networkHandler = new NetworkHandler(this);
        networkHandler.ProcessRequest(updatePinSecurityObject(customer));
    }

    private NetworkObject updatePinSecurityObject(Customer customer) {
        NetworkObject obj = new NetworkObject();
        obj.mId = NetworkConstants.CUSTOMER_PIN_SECURITY_UPDATE_SUCCESS;
        obj.mRequestUrl = NetworkConstants.UPDATE_CUSTOMER_URL;
        obj.mRequestJson = "{\"" + CustomerIdKey + "\":" + customerId + ", \""
                + FirstNameKey +  "\":\"" + firstName + "\", \""
                + LastNameKey +  "\":\"" + lastName + "\", \""
                + CellNumberKey +  "\":\"" + cellNumber + "\", \""
                + PinKey +  "\":\"" + pin + "\", \""
                + DOBKey +  "\":\"" + dob + "\", \""
                + GenderKey +  "\":" + gender + ", \""
                + SecurityQuestionIdKey +  "\":" + securityQuestionId + ", \""
                + SecurityAnswerKey +  "\":\"" + securityAnswer + "\"}";
        return obj;
    }

    @Override
    public void handleMessage(@NotNull Message message) {
        NetworkObject object = (NetworkObject) message.obj;
        switch (message.what) {
            case NetworkConstants.CUSTOMER_LOGIN_SUCCESS:
                if (message.obj != null) {
                    String json = object.mResponseJson;
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        this.customerId = jsonObject.getInt(CustomerIdKey);
                        this.firstName = jsonObject.getString(FirstNameKey);
                        this.lastName = jsonObject.getString(LastNameKey);
                        this.cellNumber = jsonObject.getString(CellNumberKey);
//                        this.pin = jsonObject.getString(PinKey);
                        this.dob = jsonObject.getString(DOBKey);
                        this.gender = jsonObject.getInt(GenderKey);
                        this.securityQuestionId = jsonObject.getInt(SecurityQuestionIdKey);
                        this.securityAnswer = jsonObject.getString(SecurityAnswerKey);
                        this.doctorName = jsonObject.getString(DoctorNameKey);
                        String registrationDate = jsonObject.getString(RegistrationDateTimeKey);
                        this.registrationDateTime = ViewUtils.getDateForFormat("yyyy-MM-dd'T'HH:mm:ss", registrationDate, "UTC");
                        this.timezoneId = (ViewUtils.getTimezoneForDate(this.registrationDateTime)).getID();
                        this.registrationDateTime = new Date(ViewUtils.getDateWithoutTimeInMillies(this.registrationDateTime, timezoneId));
                        saveObject();
                        PostNotification.sendMessage(NetworkConstants.CUSTOMER_OPERATION_FAILURE, this, mHandler);
                    } catch (JSONException e) {
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
                        this.doctorName = jsonObject.getString(DoctorNameKey);
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
            case NetworkConstants.ACCESS_CODE_SUCCESS:
                if (message.obj != null) {
                    String json = object.mResponseJson;
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        this.customerId = jsonObject.getInt(CustomerIdKey);
                        this.firstName = jsonObject.getString(FirstNameKey);
                        this.lastName = jsonObject.getString(LastNameKey);
                        this.cellNumber = jsonObject.getString(CellNumberKey);
                        PostNotification.sendMessage(NetworkConstants.ACCESS_CODE_SUCCESS, this, mHandler);
                    } catch (JSONException e) {
                        PostNotification.sendMessage(NetworkConstants.CUSTOMER_OPERATION_FAILURE, "Oops , the access code you entered is wrong. Please enter the right access code and try again.", mHandler);
                    }
                }
                else {
                    PostNotification.sendMessage(NetworkConstants.CUSTOMER_OPERATION_FAILURE, "Oops , the access code you entered is wrong. Please enter the right access code and try again.", mHandler);
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
                    String json = object.mResponseJson;
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        saveObject();
                        PostNotification.sendMessage(NetworkConstants.CUSTOMER_DETAILS_UPDATE_SUCCESS, this, mHandler);
                    } catch (JSONException e) {
                        PostNotification.sendMessage(NetworkConstants.CUSTOMER_OPERATION_FAILURE, json, mHandler);
                    }
                }

                break;

            default:
                break;
        }
    }
}