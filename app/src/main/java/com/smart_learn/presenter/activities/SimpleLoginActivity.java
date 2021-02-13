package com.smart_learn.presenter.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.smart_learn.R;
import com.smart_learn.core.config.CurrentConfig;

public class SimpleLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_login);

        // set the toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Login");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // set current context and activity
        CurrentConfig.getCurrentConfigInstance().makeNewConfig(this);

        // set listeners
        findViewById(R.id.btnLoginGuest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToOpenLessonActivity();
            }
        });

    }

    private void goToOpenLessonActivity(){
        Intent intent = new Intent(this, OpenLessonActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
        //menuInflater.inflate(R.menu.toolbar_menu, menu)
        //return true
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
        /*
        when (item.itemId) {
            R.id.btnSettings -> {
                showSettingsDialog(this)
                return true
            }
        }
        return super.onOptionsItemSelected(item)

         */
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}