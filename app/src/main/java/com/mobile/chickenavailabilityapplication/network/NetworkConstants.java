package com.mobile.chickenavailabilityapplication.network;

/**
 * Created by Harsha Reddy
 */
public class NetworkConstants {

    public static final int ACCESS_CODE_SUCCESS = 1;
    public static final int CUSTOMER_DETAILS_SAVE_SUCCESS = 2;
    public static final int CUSTOMER_LOGIN_SUCCESS = 3;
    public static final int CUSTOMER_DETAILS_UPDATE_SUCCESS = 4;
    public static final int GET_MENUITEMS_SUCCESS = 5;
    public static final int GET_MENUITEMS_FAILURE = 6;


    public static final int CUSTOMER_PIN_SECURITY_UPDATE_SUCCESS = 12;

    public static final int CUSTOMER_OPERATION_FAILURE = 101;


    public static final int NO_NETWORK_ID = 400;
    public static final int NETWORK_ERROR_ID = 401;

    public static final String NO_NETWORK_MESSAGE = "No network found!";
    public static final String UNKNOWN_ERROR_MESSAGE = "An unknown error has occured. Please try after sometime.";


    public static String SERVER_BASE_URL = "http://ec2co-ecsel-b4mx5wxkl978-1215690384.us-east-1.elb.amazonaws.com";
    //public static String SERVER_BASE_URL = "http://127.0.0.1:80";

    public static String GET_CUSTOMER_URL = SERVER_BASE_URL + "/api/CustomerService/getCustomer";
    public static String SAVE_CUSTOMER_URL = SERVER_BASE_URL + "/api/User";
    public static String UPDATE_CUSTOMER_URL = SERVER_BASE_URL + "/api/CustomerService/updateCustomerProfile";
    public static String LOGIN_CUSTOMER_URL = SERVER_BASE_URL + "/api/CustomerService/customerLogin";

}
