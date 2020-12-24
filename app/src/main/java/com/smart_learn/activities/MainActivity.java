package com.smart_learn.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.smart_learn.R;
import com.smart_learn.config.CurrentConfig;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set current context and activity
        CurrentConfig.getCurrentConfigInstance().makeNewConfig(this);

        Intent intent = new Intent(this, SimpleLoginActivity.class);
        //Intent intent = new Intent(this, GameAActivity.class);
        startActivity(intent);
    }
}