package com.mobile.chickenavailabilityapplication.network;

import com.mobile.chickenavailabilityapplication.datamodel.NetworkObject;
import com.mobile.chickenavailabilityapplication.util.Crypto;
import com.mobile.chickenavailabilityapplication.util.IOUtils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

/**
 * Created by Harsha Reddy
 */
public class NetRequestHandler {

    private SSLSocketFactory mSslSocketFactory;

    public NetRequestHandler() {
        mSslSocketFactory = Crypto.newSSLSocketFactory();
    }

    public NetworkObject doRequest(NetworkObject networkObject) {
        try {
            HttpURLConnection connection = null;
            URL url = new URL(networkObject.mRequestUrl);
            if (networkObject.mRequestUrl.startsWith("https")) {
                connection = (HttpsURLConnection) url.openConnection();
                ((HttpsURLConnection) connection).setSSLSocketFactory(mSslSocketFactory);
            }
            else {
                connection = (HttpURLConnection) url.openConnection();
            }


            if (networkObject.mRequestJson == null || networkObject.mRequestJson.length() == 0) {
                connection.setRequestMethod("GET");
            }
            else {
                /*if(networkObject.mAuthCode!= null){
                    String jwt = "JWT " + networkObject.mAuthCode;
                    connection.setRequestProperty("Authorization", jwt);
                }*/
              //  connection.setRequestProperty("authSecret", "MobileApp");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);

                if (networkObject.mRequestJson == null) {
                    networkObject.mRequestJson = "";
                }
                connection.setRequestProperty("Content-Length", Integer
                        .toString(networkObject.mRequestJson.getBytes().length));

                IOUtils.toOutputStream(networkObject.mRequestJson, connection.getOutputStream());
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {

                if (connection.getRequestMethod().equals("POST")
                        || networkObject.mId==NetworkConstants.GET_MENUITEMS_SUCCESS
                        ) {
                    byte[] responseBytes = IOUtils.toByteArrayUsingReader(connection.getInputStream());
                    networkObject.mResponseJson = new String(responseBytes);
                }
            }
            else {
                networkObject.mId = NetworkConstants.NETWORK_ERROR_ID;
                networkObject.mError = connection.getResponseMessage();
            }
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
            networkObject.mId = NetworkConstants.NETWORK_ERROR_ID;
            networkObject.mError = "Network not reachable, DNS look up failed!";
        }
        catch (Exception e) {
            networkObject.mId = NetworkConstants.NETWORK_ERROR_ID;
            networkObject.mError = e.getMessage();
        }

        return networkObject;
    }
}
