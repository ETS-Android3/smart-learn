package com.smart_learn.presenter.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.smart_learn.R;
import com.smart_learn.config.CurrentConfig;
import com.smart_learn.remote.test.sockets.LoadingConnectionDialog;
import com.smart_learn.services.TestService;
import com.smart_learn.utilities.GeneralUtilities;

public class TestGenerationActivity extends AppCompatActivity {

    private EditText etGeneratedCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_generation);

        // set the toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Test generation");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // set current context and activity
        CurrentConfig.getCurrentConfigInstance().makeNewConfig(this);

        // get View components
        etGeneratedCode = findViewById(R.id.etGeneratedCode);

        // set listeners
        findViewById(R.id.btnGenerateTest).setOnClickListener(v -> goToTestSettingsActivity());

        findViewById(R.id.btnConnectToTest).setOnClickListener(v -> {

            String code = etGeneratedCode.getText().toString();
            if (code.isEmpty()){
                GeneralUtilities.showToast("No code entered");
                return;
            }


            // check for connection and make initial setup
            /*
            if(NetworkUtilities.notGoodConnection()){
                return;
            }
             */

            // reset remote test info
            TestService.getTestServiceInstance().resetCurrentTestInfo();

            // mark that the player don`t generated the test
            TestService.getTestServiceInstance().isTestAdmin.set(false);

            // delete previous code
            TestService.getTestServiceInstance().testCode
                    .delete(0,  TestService.getTestServiceInstance().testCode.length());
            // and add new code
            TestService.getTestServiceInstance().testCode.append(code);

            // show a loading dialog while connecting to test
            LoadingConnectionDialog loadingDialog = new LoadingConnectionDialog("Connecting to test ...", "");
            // Code does no exists . Will be obtained after request made in dialog.
            loadingDialog.startTestConnection(code, TestService.getTestServiceInstance().isTestAdmin.get());

        });
    }

    private void goToTestSettingsActivity(){
        Intent intent = new Intent(this, TestSettingsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // set current context and activity
        CurrentConfig.getCurrentConfigInstance().makeNewConfig(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}