package com.mobile.chickenavailabilityapplication.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

import com.mobile.chickenavailabilityapplication.datamodel.NetworkObject;
import com.mobile.chickenavailabilityapplication.util.PostNotification;
import com.mobile.chickenavailabilityapplication.ChickenAvailabilityApplication;

/**
 * Created by HarshaReddy
 */
public class NetworkHandler extends Handler {

    private NetRequestHandler mNetRequestHandler;
    private Context mContext;
    private Handler mResponseHandler;

    public NetworkHandler(Handler responseHandler) {
        mContext = ChickenAvailabilityApplication.getContext();
        mResponseHandler = responseHandler;
        mNetRequestHandler = new NetRequestHandler();
    }

    public void ProcessRequest(NetworkObject networkObject) {
        new BackGroundTaskThread(networkObject).start();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = networkInfo == null ? false : networkInfo.isConnected();

        if (isConnected) {
            NetworkInfo[] networks = connectivityManager.getAllNetworkInfo();
            for (NetworkInfo n : networks) {
                if (n.getState() == NetworkInfo.State.CONNECTED
                        || n.getState() == NetworkInfo.State.CONNECTING) {
                    if (n.getType() == ConnectivityManager.TYPE_MOBILE
                            || n.getType() == ConnectivityManager.TYPE_WIFI) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private class BackGroundTaskThread extends Thread {

        private NetworkObject mNetworkObject;

        public BackGroundTaskThread(NetworkObject networkObject) {
            mNetworkObject = networkObject;
        }

        @Override
        public void run() {
            if (isNetworkAvailable()) {
                mNetRequestHandler.doRequest(mNetworkObject);
            } else {
                mNetworkObject.mId = NetworkConstants.NETWORK_ERROR_ID;
                mNetworkObject.mError = NetworkConstants.NO_NETWORK_MESSAGE;
            }

            PostNotification.sendMessage(mNetworkObject.mId, mNetworkObject, mResponseHandler);
        }
    }
}
