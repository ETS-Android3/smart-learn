package com.smart_learn.core.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;

import com.smart_learn.core.services.ThreadExecutorService;
import com.smart_learn.presenter.helpers.ApplicationController;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Use this to check device connexion. Network connexion and internet connexion will be checked.
 * */
public class ConnexionChecker {

    private static final int DEFAULT_TIMEOUT_MILLISECONDS = 1500;

    @NonNull @NotNull
    private final ConnexionChecker.Callback callback;
    private final int timeout;

    /**
     * Constructor to create a new ConnexionChecker.
     *
     * @param callback Callback to manage actions after check is made.
     * */
    public ConnexionChecker(@NonNull @NotNull ConnexionChecker.Callback callback) {
        this.callback = callback;
        this.timeout = DEFAULT_TIMEOUT_MILLISECONDS;
    }


    /**
     * Constructor to create a new ConnexionChecker.
     *
     * @param callback Callback to manage actions after check is made.
     * @param timeout In milliseconds. How much time should ConnexionChecker wait until to cancel
     *                internet availability checking. If in 'timeout' millisecond internet is not
     *                reachable, then check is finished.
     * */
    public ConnexionChecker(@NonNull @NotNull ConnexionChecker.Callback callback, int timeout) {
        this.callback = callback;
        this.timeout = timeout;
    }

    /**
     * Use this in order to check only if network is available. If internet is available will NOT be
     * checked by this method.
     *
     * @return true if network is available, or false otherwise.
     * */
    public static boolean isNetworkAvailable(){
        // https://stackoverflow.com/questions/4238921/detect-whether-there-is-an-internet-connection-available-on-android/4239019#4239019
        ConnectivityManager connectivityManager
                = (ConnectivityManager) ApplicationController.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Use to start connexion checking. Checks for both network availability and internet
     * availability will be made.
     * */
    public void check(){
        checkConnection();
    }

    private void checkConnection(){
        if(!isNetworkAvailable()){
            callback.networkDisabled();
            callback.notConnected();
            return;
        }

        // verify internet availability only if network connexion exists
        ThreadExecutorService.getInstance().execute(this::isInternetAvailable);
    }

    private void isInternetAvailable() {
        // https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out/17583324#17583324
        try {
            Socket sock = new Socket();
            SocketAddress sockAddress = new InetSocketAddress("8.8.8.8", 53);

            sock.connect(sockAddress, timeout);
            sock.close();

            callback.isConnected();
            return;

        } catch (IOException e) {
            e.printStackTrace();
        }

        callback.internetNotAvailable();
        callback.notConnected();
    }


    /**
     * Callback to manage actions after check is made.
     * */
    public interface Callback {

        /**
         * Will be called after check is finished and internet is reachable.
         * */
        void isConnected();

        /**
         * Will be called before notConnected() and will be called if network is not available. If
         * this will be called internetNotAvailable() will no longer be called.
         * */
        default void networkDisabled(){

        }

        /**
         * Will be called before notConnected() and will be called if internet is NOT available
         * (at this moment network is NOT disabled). If this will be called networkDisabled() will
         * no longer be called.
         * */
        default void internetNotAvailable() {

        }

        /**
         * Will be called after check is finished and network is disabled, or internet is NOT
         * reachable.
         * */
        void notConnected();
    }

}