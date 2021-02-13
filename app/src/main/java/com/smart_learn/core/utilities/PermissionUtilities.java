package com.smart_learn.core.utilities;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

import com.smart_learn.core.config.CurrentConfig;

public interface PermissionUtilities {

    static void checkPermissions(){

        String[] perms = {Manifest.permission.INTERNET};
        // https://www.geeksforgeeks.org/android-how-to-request-permissions-in-android-application/
        if(ContextCompat.checkSelfPermission(CurrentConfig.getCurrentConfigInstance().currentContext,
                Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){
            System.out.println("Permission NO accept");
        }
        else{
            System.out.println("Permission YES accept");
        }

    }
}
