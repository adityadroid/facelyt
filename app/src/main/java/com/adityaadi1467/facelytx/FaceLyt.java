package com.adityaadi1467.facelytx;

import android.app.Application;

import com.adityaadi1467.facelytx.Utilities.ConnectivityReceiver;


/**
 * Created by adi on 2/10/16.
 */
public class FaceLyt extends Application {

    private static FaceLyt mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;


    }

    public static synchronized FaceLyt getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}
