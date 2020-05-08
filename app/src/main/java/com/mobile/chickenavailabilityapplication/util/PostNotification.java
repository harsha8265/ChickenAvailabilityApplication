package com.mobile.chickenavailabilityapplication.util;

import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

/**
 * Created by Harsha on 05/02/20.
 */
public class PostNotification {

    public static void sendMessage(int what, Object obj, Handler handle) {
        if (handle == null) {
            return;
        }
        Message msg = Message.obtain();
        msg.obj = obj;
        msg.what = what;
        try {
            Messenger messenger = new Messenger(handle);
            messenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
