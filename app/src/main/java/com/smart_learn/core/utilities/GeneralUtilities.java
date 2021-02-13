package com.smart_learn.core.utilities;

import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.smart_learn.core.config.CurrentConfig;

public interface GeneralUtilities {

    static void showToast(final CharSequence message){
        CurrentConfig.getCurrentConfigInstance()
                .currentActivity.runOnUiThread(() -> Toast.makeText(
                CurrentConfig.getCurrentConfigInstance().currentActivity.getApplicationContext(),
                message,Toast.LENGTH_LONG).show());
    }

    static void notImplemented() {
        Log.e(Logs.ERROR, "Not implemented here");
        try {
            Thread.sleep(50000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /** Helper to print in json format for logging purpose. */
    static String stringToPrettyJson(String requestResponse){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(requestResponse);
        return gson.toJson(jsonElement);
    }
}

