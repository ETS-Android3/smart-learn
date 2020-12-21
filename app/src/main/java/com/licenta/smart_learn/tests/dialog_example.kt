package com.licenta.smart_learn.tests

import android.app.Activity
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

fun alertDialogExample(activity: Activity){

    val builder = AlertDialog.Builder(activity)
    //set title for alert dialog
    builder.setTitle("titlu")
    //set message for alert dialog
    builder.setMessage("mesaj")
    builder.setIcon(android.R.drawable.ic_dialog_alert)

    //performing positive action
    builder.setPositiveButton("Yes"){dialogInterface, which ->
        Toast.makeText(activity,"clicked yes", Toast.LENGTH_LONG).show()
    }
    //performing cancel action
    builder.setNeutralButton("Cancel"){dialogInterface , which ->
        Toast.makeText(activity,"clicked cancel\n operation cancel", Toast.LENGTH_LONG).show()
    }
    //performing negative action
    builder.setNegativeButton("No"){dialogInterface, which ->
        Toast.makeText(activity,"clicked No", Toast.LENGTH_LONG).show()
    }
    // Create the AlertDialog
    val alertDialog: AlertDialog = builder.create()
    // Set other dialog properties
    alertDialog.setCancelable(false)
    alertDialog.show()

}