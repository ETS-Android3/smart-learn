package com.smart_learn.presenter.activities.authentication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.R;
import com.smart_learn.databinding.ActivityAuthenticationBinding;

public class AuthenticationActivity extends AppCompatActivity {

    private AuthenticationSharedViewModel sharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAuthenticationBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_authentication);
        binding.setLifecycleOwner(this);

        setViewModel();
        binding.setSharedViewModel(sharedViewModel);

        setSupportActionBar(binding.authToolbar);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setViewModel(){
        // set shared view model for passing data
        sharedViewModel = new ViewModelProvider(this).get(AuthenticationSharedViewModel.class);
    }
}