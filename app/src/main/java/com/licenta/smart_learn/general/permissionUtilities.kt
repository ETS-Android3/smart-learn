package com.licenta.smart_learn.general

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat.checkSelfPermission
import com.google.android.material.snackbar.Snackbar

/**
* INFO (about permissions):
*      - https://developer.android.com/training/permissions/requesting
*      - https://github.com/googlearchive/android-RuntimePermissions (official example)
*
*
* INFO (about external storage types)
*     - https://imnotyourson.com/which-storage-directory-should-i-use-for-storing-on-android-6/
*     - https://stackoverflow.com/questions/5092591/what-are-the-differences-among-internal-storage-external-storage-sd-card-and-r
*     - https://developer.android.com/training/data-storage
* */

const val ACTION_OK = "OK"
const val SUCCESS_DATA_LOADED_MESSAGE = "Data loaded"
const val NOT_GRANTED_ERROR = " [permission has NOT been granted]"

const val READ_EXTERNAL_STORAGE_PERMISSION_CODE : Int = 1
const val WRITE_EXTERNAL_STORAGE_PERMISSION_CODE : Int = 2
const val TAG_READ_EXTERNAL_STORAGE_PERMISSION = "READ_EXTERNAL_STORAGE permission"
const val TAG_WRITE_EXTERNAL_STORAGE_PERMISSION = "WRITE_EXTERNAL_STORAGE permission"
const val ERROR_MESSAGE_READ_EXTERNAL_STORAGE_PERMISSION = "[ERROR] The permission for read has NOT been granted! Add permission!"
const val ERROR_MESSAGE_WRITE_EXTERNAL_STORAGE_PERMISSION = "[ERROR] The permission for write has NOT been granted! Add permission!"


const val ERROR_EXTERNAL_STORAGE_MOUNTED = "[INTERNAL ERROR] External storage is not mounted"
const val ERROR_EXTERNAL_STORAGE_READ_ONLY = "[INTERNAL ERROR] External storage is read only"

fun requestPermission(activity: Activity, permission: String, permissionCode: Int,
                      snackBarLayoutActivity: View, requestPermission: Boolean): Boolean{

    // if permission has NOT been granted request permission if requestPermission is true
    if (checkSelfPermission(activity.applicationContext,permission) == PackageManager.PERMISSION_DENIED && requestPermission) {

        if (shouldShowRequestPermissionRationale(activity, permission)) {
            // Permission has been declined before. Check if user wants this now and show additional
            // info for this request

            Snackbar.make(
                snackBarLayoutActivity,
                "$permission permission is needed",
                Snackbar.LENGTH_INDEFINITE
            )
                .setAction(ACTION_OK) {
                    requestPermissions(
                        activity,
                        arrayOf(permission),
                        permissionCode
                    )
                }.show()

        }
        else {
            // Permission has not been granted yet. Request it directly.
            requestPermissions(activity, arrayOf(permission), permissionCode)
        }
    }

    println("-----")
    // after the previous request check if the user gives the permission
    return checkSelfPermission(activity.applicationContext,permission) == PackageManager.PERMISSION_GRANTED
}

fun isReadFromExternalStoragePermission(activity: Activity, snackBarLayoutActivity: View,
                                        requestPermission: Boolean): Boolean{

    // check if an external storage device exists
    if(!isExternalStorageMounted()) {
        //Log.e(Environment.MEDIA_MOUNTED, ERROR_EXTERNAL_STORAGE_MOUNTED)
        Toast.makeText(activity, ERROR_EXTERNAL_STORAGE_MOUNTED,
            Toast.LENGTH_LONG).show()
        return false
    }

    // check for read permission
    if(!requestPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE,
            READ_EXTERNAL_STORAGE_PERMISSION_CODE, snackBarLayoutActivity, requestPermission)
    ){

        // TODO: check for synchronized call
        // Attention to Log message and Toast.make:
        // Log will be executed before requestPermission(...) finishes because requestPermission(...)
        // is working in other thread ( pop up's with permission request will appear on screen )
        // and message will be logged in the same time.

        // Log.e(TAG_READ_EXTERNAL_STORAGE_PERMISSION, NOT_GRANTED_ERROR)

        if(!requestPermission) {
            Toast.makeText(
                activity, ERROR_MESSAGE_READ_EXTERNAL_STORAGE_PERMISSION,
                Toast.LENGTH_LONG
            ).show()
        }

        return false
    }

    return true

}

fun isWriteToExternalStoragePermission(activity: Activity, snackBarLayoutActivity: View,
                                       requestPermission: Boolean): Boolean{

    // check if an external storage device exists
    if(!isExternalStorageMounted()) {
        //Log.e(Environment.MEDIA_MOUNTED, ERROR_EXTERNAL_STORAGE_MOUNTED)
        Toast.makeText(activity, ERROR_EXTERNAL_STORAGE_MOUNTED,
            Toast.LENGTH_LONG).show()
        return false
    }

    // check if this external storage device can support writing
    if(!isExternalStorageWritable()) {
        //Log.e(Environment.MEDIA_MOUNTED_READ_ONLY, ERROR_EXTERNAL_STORAGE_READ_ONLY)
        Toast.makeText(activity, ERROR_EXTERNAL_STORAGE_READ_ONLY,
            Toast.LENGTH_LONG).show()
        return false
    }

    // check for write permission
    if(!requestPermission(activity,Manifest.permission.WRITE_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE_PERMISSION_CODE,snackBarLayoutActivity,requestPermission)
    ){

        // TODO: check for synchronized call
        // Attention to Log message and Toast.make:
        // Log will be executed before requestPermission(...) finishes because requestPermission(...)
        // is working in other thread ( pop up's with permission request will appear on screen )
        // and message will be logged in the same time.

        //Log.e(TAG_WRITE_EXTERNAL_STORAGE_PERMISSION, NOT_GRANTED_ERROR)

        if(!requestPermission) {
            Toast.makeText(
                activity, ERROR_MESSAGE_WRITE_EXTERNAL_STORAGE_PERMISSION,
                Toast.LENGTH_LONG
            ).show()
        }

        return false
    }

    return true

}


fun isExternalStorageMounted() : Boolean{
    // even if the external storage is mounted it still cannot be read from it without permissions
    // in fact here is checked only if an external storage device exists
    return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
}

fun isExternalStorageWritable() : Boolean{
    // check if an external storage device is with read only capability
    // This read only is not related to read or write permission. Is a hardware attribute.
    // TODO: check for more info about storage VS permissions
    return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED_READ_ONLY
}