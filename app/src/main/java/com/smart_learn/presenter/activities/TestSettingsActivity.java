package com.smart_learn.presenter.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.smart_learn.R;
import com.smart_learn.core.config.CurrentConfig;
import com.smart_learn.core.services.TestService;

public class TestSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_settings);

        // set toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Online Test Settings");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // set current context and activity
        CurrentConfig.getCurrentConfigInstance().makeNewConfig(this);

        findViewById(R.id.btnStartTest).setOnClickListener(v -> TestService.getTestServiceInstance().createRemoteConnection());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // set current context and activity
        CurrentConfig.getCurrentConfigInstance().makeNewConfig(this);
    }
}