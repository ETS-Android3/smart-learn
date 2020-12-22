package com.licenta.smart_learn.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.licenta.smart_learn.config.CurrentConfig;
import com.licenta.smart_learn.remote.api.HttpRequestService;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public abstract class NetworkUtilities {

    public final static String NO_IP_FOUND = "";
    private static StringBuffer devicePublicIpAddress = new StringBuffer();

    public static boolean notGoodConnection(){

        // check for internet access
        if(!(isNetworkAvailable() && isInternetAvailable())){
            GeneralUtilities.showToast("No internet connection !");
            return true;
        }

        Log.i(Logs.INFO,"Internet connexion exist");
        return false;
    }



    // https://stackoverflow.com/questions/4238921/detect-whether-there-is-an-internet-connection-available-on-android/4239019#4239019
    private static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) CurrentConfig.getCurrentConfigInstance().currentContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //https://stackoverflow.com/questions/9570237/android-check-internet-connection/9570292#9570292
    private static boolean isInternetAvailable() {
        try {
            InetAddress ipAddress = InetAddress.getByName("google.com");
            System.out.println(ipAddress);
            System.out.println(ipAddress.toString());
            return !ipAddress.toString().equals("");
        } catch (Exception e) {
            return false;
        }
    }

    public static void findPublicIPAddress(){
        // TODO: request ping url from api server
        String url = "http://whatismyip.akamai.com/";

        // delete current publicIp
        devicePublicIpAddress.delete(0,devicePublicIpAddress.length());

        // make request to find new public ip (resulting public ip can be the same as previous)
        HttpRequestService.getHttpRequestServiceInstance()
                .sendGetRequestOkHttp3(url, HttpRequestService.PUBLIC_IP_REQUEST_ERROR,
                        new HashMap<>(), requestResponse -> {
                            try {
                                devicePublicIpAddress.append(requestResponse);
                                Log.i("Public Ip: ", devicePublicIpAddress.toString());
                            }
                            catch (final Exception e){
                                e.printStackTrace();
                                devicePublicIpAddress.append(NO_IP_FOUND);
                                Log.e(Logs.API_REQ_ERROR_TAG, "Failed to get public IP [" + e + "]");
                            }
                        });
    }

    // TODO: check this function and understand it
    public static String getLocalIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4) {
                                return sAddr;
                            }
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {
            Log.e("[ERROR]"," no local ip found");
        } // for now eat exceptions
        return "";
    }

    public static StringBuffer getDevicePublicIpAddress() {
        return devicePublicIpAddress;
    }
}

