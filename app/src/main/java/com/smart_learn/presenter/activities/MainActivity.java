package com.smart_learn.presenter.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.smart_learn.R;
import com.smart_learn.core.config.CurrentConfig;
import com.smart_learn.core.services.TestService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set current context and activity
        CurrentConfig.getCurrentConfigInstance().makeNewConfig(this);

        //Intent intent = new Intent(this, SimpleLoginActivity.class);
        //startActivity(intent);

        TestService.getTestServiceInstance().currentTestMode.set(TestService.REMOTE_MODE_TEST);
        //dictionaryActivity.startTestGenerationActivity()
        Intent intent = new Intent(this, TestGenerationActivity.class);
        startActivity(intent);
    }
}