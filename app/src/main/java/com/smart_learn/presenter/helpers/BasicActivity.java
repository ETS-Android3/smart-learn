package com.smart_learn.presenter.helpers;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * The main activity from which all the activities of the application must be extended.
 * */
public abstract class BasicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}