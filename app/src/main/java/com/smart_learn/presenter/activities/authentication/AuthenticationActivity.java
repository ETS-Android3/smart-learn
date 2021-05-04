package com.smart_learn.presenter.activities.authentication;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.R;
import com.smart_learn.databinding.ActivityAuthenticationBinding;

public class AuthenticationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAuthenticationBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_authentication);
        binding.setLifecycleOwner(this);

        setSupportActionBar(binding.toolbarAuthenticationActivity);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /** Used for resetting toolbar title, from fragments. */
    public void resetToolbar(String title){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle(title);
        }
    }

}