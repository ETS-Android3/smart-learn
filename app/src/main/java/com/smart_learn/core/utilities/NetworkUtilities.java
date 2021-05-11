package com.smart_learn.core.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.smart_learn.presenter.helpers.ApplicationController;

public abstract class NetworkUtilities {

    /** Check only if device is connected to a network. Check for internet access is not made.
     *
     * TODO: check for internet access also (e.g. a device can be connected to a network,
     *    but network does not have internet access)
     * */
    public static boolean goodConnection(){
        return isNetworkAvailable();
    }

    private static boolean isNetworkAvailable() {
        // https://stackoverflow.com/questions/4238921/detect-whether-there-is-an-internet-connection-available-on-android/4239019#4239019
        ConnectivityManager connectivityManager
                = (ConnectivityManager) ApplicationController.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}

